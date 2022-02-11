//Cristian Trandafir

import scala.collection.mutable

object SetTheory:
  private val setMap: mutable.Map[String, Any] = mutable.Map[String, Any]()
  private val macroMap: mutable.Map[String, ArithExp] = mutable.Map[String, ArithExp]()
  //private val scopeMap: mutable.Map[String, String] = mutable.Map[String, String]("main" -> "main")
  //private val currentScope: mutable.Map[String, String] = mutable.Map[String, String]("main" -> "main")
  //private val currentMap: mutable.Map[String, Any] = mutable.Map[String, Any]("current" -> setMap)
  enum ArithExp:
    case Variable(obj: Any)
    case Identifier(name: String)
    case Insert(op1: Identifier, op2: Variable)
    case Delete(op1: Identifier, op2: Identifier)
    case Assign(op1: Identifier, op2: Insert)
    case Check(op1: Identifier, op2: Identifier)
    //Set operations
    case Union(op1: Identifier, op2: Identifier)
    case Intersection(op1: Identifier, op2: Identifier)
    case Difference(op1: Identifier, op2: Identifier)
    case Symmetric(op1: Identifier, op2: Identifier)
    case Product(op1: Identifier, op2: Identifier)
    //Macro
    case Macro(op1: Identifier, op2: ArithExp)
    case UseMacro(op1: Identifier)
    //Scope
    //case Scope(op1: Identifier, op2: Identifier, op3: ArithExp)

    //Helper method that returns a tuple containing 2 sets to the set operation functions in eval
    private def getExistingSets(setName1: Identifier, setName2: Identifier): (Set[(String, Any)], Set[(String, Any)]) =
      val firstSetName = setName1.eval.asInstanceOf[String]
      val secondSetName = setName2.eval.asInstanceOf[String]
      if(!setMap.contains(firstSetName)) {
        throw new RuntimeException("First set not found")
      }
      if(!setMap.contains(secondSetName)) {
        throw new RuntimeException("Second set not found")
      }
      val firstSet = setMap(firstSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      val secondSet = setMap(secondSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      (firstSet, secondSet)

    /*
    def recursiveResolveScope(map: mutable.Map[String, Any], scopeHierarchyString: String): mutable.Map[String, Any] =
      //If scope string still has "." delimiter - advance the scope from main -> desiredScope by 1
      if(scopeHierarchyString.contains(".")) {
        //main.scope1.scope2 would turn to scope1.scope2
        val reducedString = scopeHierarchyString.substring(scopeHierarchyString.indexOf("."), scopeHierarchyString.length)
        //This command would be main(reducedString) which returns the scope1Map that contains scope2Map
        val newMap = map(reducedString).asInstanceOf[mutable.Map[String, Any]]
        //Recurse until you get to .scope2, or whatever the last scope is
        recursiveResolveScope(newMap, reducedString)
      }
      //If scope string has no "." delimiter - this is the last scope. Simply return current scope as a map.
      else {
        map
      }

    def resolveScopeToMain(map: mutable.Map[String, Any], scopeHierarchyString: String, searchString: String): mutable.Map[String, Any] =
      if(scopeHierarchyString.contains(".")) {
        //main.scope1.scope2 would turn to main.scope1
        val reducedString = scopeHierarchyString.substring(0, scopeHierarchyString.lastIndexOf("."))
        //This command would be main(reducedString) which returns the scope1Map that contains scope2Map
        val newMap = recursiveResolveScope(map, reducedString)
        //Recurse until you get to main, or if variable found
        if(!newMap.contains(searchString))
          resolveScopeToMain(newMap, reducedString, searchString)
        else
          newMap
      }
      //If scope string has no "." delimiter - this is the first scope. Simply return main scope.
      else {
        setMap
      }
    */

    def eval: Any =
      this match{

        //Just a wrapper method that returns the object passed in
        case Variable(obj) => obj

        //Returns (String, Any) tuple for insertion
        case Insert(op1:Identifier, op2:Any) => (op1.eval, op2.eval)

        //Inserts op2 into set name found in setMap
        case Assign(name: Identifier, insertCommand: Insert) =>
          val setName = name.eval.asInstanceOf[String]
          val tupleAsAny = insertCommand.eval
          //In-line function that reseparates the Any into a tuple
          def function(msg: Any): (String, Any) = {
            msg match {
              case (a: String, b: Any) => (a, b)
            }
          }
          val stringAnyTuple = function(tupleAsAny)
          if(!setMap.contains(setName)){
            setMap += (setName -> mutable.Map[String,Any](stringAnyTuple._1 -> stringAnyTuple._2))
          }
          else {
            val set = setMap(setName).asInstanceOf[mutable.Map[String, Any]]
            if(set.contains(stringAnyTuple._1)){
              throw new RuntimeException("Failed to insert into set.")
            }
            else {
              set += (stringAnyTuple._1 -> stringAnyTuple._2)
            }
          }

        //Returns a helpful message to the user about the (non)existence of the two set names
        case Check(op1, op2) =>
          val setName = op1.eval.asInstanceOf[String]
          val objectName = op2.eval.asInstanceOf[String]
          if(!setMap.contains(setName)){
            "Set " + setName + " does not exist."
          }
          else {
            val set = setMap(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
            if(set.contains(objectName))
              "Set " + setName + " does contain " + objectName + "."
            else {
              "Set " + setName + " does not contain " + objectName + "."
            }
          }

        //Just a wrapper method that returns the name as a string
        case Identifier(name:String) => name

        //Deletes object op2 from set op1
        case Delete(setIdentifier: Identifier, objectIdentifier: Identifier) =>
          val setName = setIdentifier.eval.asInstanceOf[String]
          val objectName = objectIdentifier.eval.asInstanceOf[String]
          if(!setMap.contains(setName)) {
            return "Set " + setName + " does not exist."
          }
          else {
            val set = setMap(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
            if(!set.contains(objectName))
              return "Object " + objectName + " does not exist inside " + setName + "."
          }
          val set = setMap(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
          set -= objectName
          "Successful deletion of " + objectName + " from " + setName + "."

        //Union of 2 sets
        case Union(setName1:Identifier, setName2:Identifier) =>
          val setTuple = getExistingSets(setName1,setName2)
          val unionSet = setTuple._1.union(setTuple._2)
          val immutableUnionMap = unionSet.toMap
          val newUnionMap = mutable.Map() ++ immutableUnionMap
          newUnionMap

        //Intersection of 2 sets
        case Intersection(setName1:Identifier, setName2:Identifier) =>
          val setTuple = getExistingSets(setName1,setName2)
          val intersectionSet = setTuple._1.intersect(setTuple._2)
          val immutableIntersectionMap = intersectionSet.toMap
          val newIntersectionMap = mutable.Map() ++ immutableIntersectionMap
          newIntersectionMap

        //Symmetric Difference of 2 sets
        case Symmetric(setName1:Identifier, setName2:Identifier) =>
          val setTuple = getExistingSets(setName1,setName2)
          val symmetricSet = setTuple._1.diff(setTuple._2) union setTuple._2.diff(setTuple._1)
          val immutableIntersectionMap = symmetricSet.toMap
          val newSymmetricMap = mutable.Map() ++ immutableIntersectionMap
          newSymmetricMap

        //Difference of 2 sets
        case Difference(setName1:Identifier, setName2:Identifier) =>
          val setTuple = getExistingSets(setName1,setName2)
          val differenceSet = setTuple._1.diff(setTuple._2)
          val immutableDifferenceMap = differenceSet.toMap
          val newDifferenceMap = mutable.Map() ++ immutableDifferenceMap
          newDifferenceMap

        //Cartesian Product of 2 sets
        case Product(setName1:Identifier, setName2:Identifier) =>
          val secondSetName = setName2.eval.asInstanceOf[String]
          val firstSetName = setName1.eval.asInstanceOf[String]
          val cartesianMap = mutable.Map[String,Any]()
          getExistingSets(setName1, setName2) //Checks if sets exist
          for((k,v) <- setMap(firstSetName).asInstanceOf[mutable.Map[String, Any]]) {
            for((keys,values) <- setMap(secondSetName).asInstanceOf[mutable.Map[String, Any]])
              cartesianMap += (k+keys -> (v, values))
          }
          cartesianMap

        case Macro(name:Identifier, expression: ArithExp) =>
          if(macroMap.contains(name.eval.asInstanceOf[String]))
            macroMap(name.eval.asInstanceOf[String]) = expression
          macroMap += (name.eval.asInstanceOf[String] -> expression)

        case UseMacro(name:Identifier) =>
          if(macroMap.contains(name.eval.asInstanceOf[String])) {
            macroMap(name.eval.asInstanceOf[String]).eval
          }
          else{
            throw new RuntimeException("Macro doesn't exist")
          }
      }

  @main def createSetTheoryInputSession(): Unit =
    import ArithExp.*
    /*
    val testAssign = Assign(Identifier("Name1"), Insert(Identifier("key1"), Variable(1))).eval
    val testAssign3 = Assign(Identifier("Name1"), Insert(Identifier("key11"), Variable(11))).eval
    val testAssign2 = Assign(Identifier("Name2"), Insert(Identifier("key2"), Variable(2))).eval
    val testAssign4 = Assign(Identifier("Name2"), Insert(Identifier("key22"), Variable(22))).eval
    println(testAssign)
    val testDelete = Delete(Identifier("Name1"), Identifier("key")).eval2
    println(testDelete)
    val testUnion = Union(Identifier("Name1"), Identifier("Name2")).eval
    println(testUnion)
    val testCartesianProduct = Product(Identifier("Name1"), Identifier("Name2")).eval
    println(testCartesianProduct)

    Macro(Identifier("a"), Assign(Identifier("Name10"), Insert(Identifier("key10"), Variable(1)))).eval
    val MacroResult = UseMacro(Identifier("a")).eval
    println(MacroResult)
    */



