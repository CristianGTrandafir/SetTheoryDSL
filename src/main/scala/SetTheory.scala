//Cristian Trandafir

import scala.annotation.tailrec
import scala.collection.mutable

object SetTheory:
  private val setMap: mutable.Map[String, Any] = mutable.Map[String, Any]("main" -> mutable.Map[String, Any]())
  private val macroMap: mutable.Map[String, ArithExp] = mutable.Map[String, ArithExp]()
  //main.scope1 -> main
  private val scopeMap: mutable.Map[String, String] = mutable.Map[String, String]("main" -> "main")
  //current -> main.scope1
  private val currentScope: mutable.Map[String, String] = mutable.Map[String, String]("current" -> "main")
  //"current" -> map
  private val currentMap: mutable.Map[String, Any] = mutable.Map[String, Any]("current" -> setMap)
  enum ArithExp:
    case Variable(obj: Any)
    case Identifier(name: String)
    case Insert(objectName: Identifier, obj: Variable)
    case Delete(setName: Identifier, objectName: Identifier)
    case Assign(setName: Identifier, insert: Insert)
    case Check(setName: Identifier, objectName: Identifier)
    //Set operations
    case Union(setName1: Identifier, setName2: Identifier)
    case Intersection(setName1: Identifier, setName2: Identifier)
    case Difference(setName1: Identifier, setName2: Identifier)
    case Symmetric(setName1: Identifier, setName2: Identifier)
    case Product(setName1: Identifier, setName2: Identifier)
    //Macro
    case Macro(name: Identifier, command: ArithExp)
    case UseMacro(name: Identifier)
    //Scope
    case Scope(newScope: Identifier, parentScope: Identifier, command: ArithExp)

    //Helper method that returns a tuple containing 2 sets to the set operation functions in eval
    private def getExistingSets(setName1: Identifier, setName2: Identifier): (Set[(String, Any)], Set[(String, Any)]) =
      val firstSetName = setName1.eval.asInstanceOf[String]
      val secondSetName = setName2.eval.asInstanceOf[String]
      //println("setMap:" + setMap)
      //println("currentMap: " + currentMap("current").asInstanceOf[mutable.Map[String, Any]])
      //println("currentScope('current'): " + currentScope("current"))
      val a = scopeMap(currentScope("current"))
      val inScopeMap = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"),firstSetName,currentMap("current").asInstanceOf[mutable.Map[String, Any]])
      if(!inScopeMap.contains(firstSetName))
        throw new RuntimeException("First set not found")
      val inScopeMap2 = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"),secondSetName,currentMap("current").asInstanceOf[mutable.Map[String, Any]])
      if(!inScopeMap2.contains(secondSetName))
        throw new RuntimeException("Second set not found")
      val firstSet = inScopeMap(firstSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      val secondSet = inScopeMap2(secondSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      (firstSet, secondSet)

    //Starts from main and traverses setMap to find specified scope in scopeHierarchyString
    @tailrec
    private def recursiveResolveScope(map: mutable.Map[String, Any], scopeHierarchyString: String): mutable.Map[String, Any] =
    //If scope string still has "." delimiter - advance the scope from main.scope1 -> scope1
      if(scopeHierarchyString.contains(".")) {
        //main.scope1.scope2 would turn to scope1.scope2
        val reducedString = scopeHierarchyString.substring(scopeHierarchyString.indexOf(".")+1, scopeHierarchyString.length)
        //main.scope1.scope would turn to main
        val nextScope =  scopeHierarchyString.substring(0, scopeHierarchyString.indexOf("."))
        //This command would be main(reducedString) which returns the scope1Map that contains scope2Map
        if(!map.contains(nextScope)) {
          throw new RuntimeException("One of your scopes does not exist." + nextScope)
        }
        val newMap = map(nextScope).asInstanceOf[mutable.Map[String, Any]]
        //println("Last returned map from main to scope: " + newMap)
        //Recurse until you get to .scope2, or whatever the last scope is
        recursiveResolveScope(newMap, reducedString)
      }
      //If scope string has no "." delimiter - this is the last scope.
      else {
        //No past usages found so just return current scope
        if(!map.contains(scopeHierarchyString)) {
          currentMap("current").asInstanceOf[mutable.Map[String,Any]]
        }
        //Found
        else
          map(scopeHierarchyString).asInstanceOf[mutable.Map[String,Any]]
      }

    //Starts from current scope and searches for the string through the parent scopes of current scope. Returns current scope if searchString is not found.
    @tailrec
    private def resolveScopeToMain(map: mutable.Map[String, Any], scopeHierarchyString: String, searchString: String, highestLevelMap: mutable.Map[String, Any]): mutable.Map[String, Any] =
    //If found in current scope
      if(map.contains(searchString)) {
        //println("Found")
        map
      }
      //If not found in current scope, recurse towards main.
      else {
        //We are in main scope, else in some subscope
        if(!scopeHierarchyString.contains(".")) {
          //println("In main")
          return highestLevelMap

        }
        //main.scope1.scope2 would turn to main.scope1
        val reducedString = scopeHierarchyString.substring(0, scopeHierarchyString.lastIndexOf("."))
        //println("reducedString in recurs: " + reducedString)
        val firstString = scopeHierarchyString.substring(0, scopeHierarchyString.indexOf("."))
        //This makes newMap equal to the parent Map the current Map is stored in
        val newMap = recursiveResolveScope(setMap, reducedString)
        //println("New map: " + newMap)
        resolveScopeToMain(newMap, reducedString, searchString, highestLevelMap)
      }

    def eval: Any =
      this match{
        case Scope(newScope:Identifier, parentScope: Identifier, op3: ArithExp) =>
          //Parent scope does not exist
          if(!scopeMap.contains(parentScope.eval.asInstanceOf[String]))
            throw new RuntimeException("Specified scope does not exist.")
          //'.' is delimiter for Strings that keep track of scope, don't include
          if(newScope.eval.asInstanceOf[String].contains(".")) {
            if(!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String]))
              throw new RuntimeException("Please do not include '.' in your scope name.")
          }
          //Not creating new scope, just editing specified one
          if(!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String])) {
            //Include parentScope.newScope in map pointing to parentScope
            scopeMap += (parentScope.eval.asInstanceOf[String]+"."+newScope.eval.asInstanceOf[String] -> parentScope.eval.asInstanceOf[String])
          }
          //Assign class variables
          currentMap("current") = recursiveResolveScope(setMap, parentScope.eval.asInstanceOf[String]+"." + newScope.eval.asInstanceOf[String])
          //println("currentMap in Scope: " + currentMap("current"))
          if(!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String]))
            currentScope("current") = parentScope.eval.asInstanceOf[String]+"." + newScope.eval.asInstanceOf[String]
          val returnArithExp = op3.eval
          //Reset so ArithExp commands without Scope in them don't get messed up.
          currentMap("current") = setMap
          currentScope("current") = "main"
          returnArithExp

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
          //Need to create new scope
          val reducedScope = scopeMap(currentScope("current")) //main.scope1 -> main, main -> main
          //println("reducedScope: " + reducedScope)
          val fullScope = currentScope("current") //main.scope1 -> main.scope1
          //println("fullScope: " + fullScope)
          //reducedScopeMap
          //println("setName: " + setName)
          if(currentMap("current").equals(setMap)) {
            currentMap("current") = setMap("main")
          }
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], fullScope, setName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          ////println("Actual map: " + actualMapInScope)
          ////println("mapInScope: " + mapInScope)
          //println("setMap: " + setMap)
          //main.scope1 -> scope1
          //println("fullScope: " + fullScope)
          if(!fullScope.equals("main")) {
            val onlyNewScope = fullScope.substring(fullScope.lastIndexOf(".")+1, fullScope.length)
            //If there's already a scope or set with this name, error out
            if(currentMap("current").asInstanceOf[mutable.Map[String, Any]].contains(onlyNewScope))
              throw new RuntimeException("There is already a set or scope with this name.\nIf you want to access a scope and not create a new one, type the same scope name for both fields.")
            //println("onlyNewScope: " + onlyNewScope)
            //Insert new map of maps into scope for (set name -> (obj name -> obj))
            if(!mapInScope.contains(onlyNewScope)) {
              mapInScope += (onlyNewScope -> mutable.Map[String, mutable.Map[String, Any]](setName -> mutable.Map[String, Any](stringAnyTuple._1 -> stringAnyTuple._2)))
            }
            ////println("This is the map with the new scope: " + actualMapInScope)
            mapInScope
          }
          //Main special case
          else {
            val onlyNewScope = fullScope
            //println("onlyNewScope: " + onlyNewScope)
            if (!mapInScope.contains(setName)) {
              mapInScope += (setName -> mutable.Map[String, Any](stringAnyTuple._1 -> stringAnyTuple._2))
              ////println("This is the map with the new scope: " + actualMapInScope)
            }
            else {
              val set = mapInScope(setName).asInstanceOf[mutable.Map[String, Any]]
              if (set.contains(stringAnyTuple._1)) {
                throw new RuntimeException("Failed to insert duplicate value " + stringAnyTuple._1 + " into set " + setName +".")
              }
              else {
                set += (stringAnyTuple._1 -> stringAnyTuple._2)
              }
            }
          }

        //Returns a helpful message to the user about the (non)existence of the two set names
        case Check(firstSetName: Identifier, firstObjectName: Identifier) =>
          val setName = firstSetName.eval.asInstanceOf[String]
          val objectName = firstObjectName.eval.asInstanceOf[String]
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), setName,currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          //println(mapInScope)
          if(!mapInScope.contains(setName)){
            "Set " + setName + " does not exist."
          }
          else {
            val set = mapInScope(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
            if(set.contains(objectName))
              "Set " + setName + " does contain " + objectName + "."
            else {
              "Set " + setName + " does not contain " + objectName + "."
            }
          }

        //Just a wrapper method that returns the name as a string
        case Identifier(name:String) => name

        //Deletes objectIdentifier from setIdentifier
        case Delete(setIdentifier: Identifier, objectIdentifier: Identifier) =>
          val setName = setIdentifier.eval.asInstanceOf[String]
          val objectName = objectIdentifier.eval.asInstanceOf[String]
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), setName,currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          if(!mapInScope.contains(setName)) {
            return "Set " + setName + " does not exist. "
          }
          else {
            val set = mapInScope(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
            if(!set.contains(objectName))
              return "Object " + objectName + " does not exist inside " + setName + "."
          }
          val set = mapInScope(setName).asInstanceOf[mutable.Map[String,mutable.Map[String, Any]]]
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
          val firstSet = recursiveResolveScope(currentMap("current").asInstanceOf[mutable.Map[String, Any]], firstSetName)
          val secondSet = recursiveResolveScope(currentMap("current").asInstanceOf[mutable.Map[String, Any]], secondSetName)
          //println(secondSet)
          for((k,v) <- firstSet) {
            for((keys,values) <- secondSet)
              cartesianMap += (k+keys -> (v, values))
          }
          cartesianMap

        case Macro(name:Identifier, expression: ArithExp) =>
          //Overwrite macroMap
          if(macroMap.contains(name.eval.asInstanceOf[String]))
            return macroMap(name.eval.asInstanceOf[String]) = expression
          //New addition
          macroMap += (name.eval.asInstanceOf[String] -> expression)

        case UseMacro(name:Identifier) =>
          if(macroMap.contains(name.eval.asInstanceOf[String])) {
            macroMap(name.eval.asInstanceOf[String]).eval
          }
          else{
            throw new RuntimeException("Macro doesn't exist.")
          }
      }


  @main def createSetTheoryInputSession(): Unit =
    import ArithExp.*
/*
    Assign(Identifier("set10"), Insert(Identifier("var1"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var2"), Variable(1))).eval
    Assign(Identifier("set10"), Insert(Identifier("var3"), Variable(1))).eval
    Assign(Identifier("set20"), Insert(Identifier("var4"), Variable(1))).eval
    Scope(Identifier("newScope3"),Identifier("main"), Assign(Identifier("set30"), Insert(Identifier("var4"), Variable(1)))).eval
    //set10 is in parent scope of set23. This should throw an error if it fails - success if program checks parent scopes.
    Scope(Identifier("newScope3"),Identifier("main"), Union(Identifier("set10"), Identifier("set30"))).eval

    val testAssign = Assign(Identifier("Name1Set"), Insert(Identifier("key1"), Variable(1))).eval
    val testAssign3 = Assign(Identifier("Name1Set"), Insert(Identifier("key11"), Variable(11))).eval
    val testAssign2 = Assign(Identifier("Name2Set"), Insert(Identifier("key2"), Variable(2))).eval
    val testAssign4 = Assign(Identifier("Name2Set"), Insert(Identifier("key22"), Variable(22))).eval
    ////println(testAssign)
    //val testDelete = Delete(Identifier("Name1"), Identifier("key")).eval2
    ////println(testDelete)

    val testUnion = Union(Identifier("Name1Set"), Identifier("Name2Set")).eval
    ////println("testUnion: " + testUnion)
    val testCartesianProduct = Product(Identifier("Name1Set"), Identifier("Name2Set")).eval
    //println("testCartesianProduct: " + testCartesianProduct)

    Macro(Identifier("a"), Assign(Identifier("Name10"), Insert(Identifier("key10"), Variable(1)))).eval
    val MacroResult = UseMacro(Identifier("a")).eval
    //println(MacroResult)

    val testScope = Scope(Identifier("newScope"), Identifier("main"), Assign(Identifier("Scope1Set"), Insert(Identifier("key1"), Variable(1)))).eval
    val testScope2 = Scope(Identifier("newScope2"), Identifier("main.newScope"), Assign(Identifier("Scope2Set"), Insert(Identifier("key1"), Variable(1)))).eval
    //println("scopeMap: " + scopeMap)
    Scope(Identifier("newScope3"),Identifier("main.newScope.newScope2"), Assign(Identifier("set20"), Insert(Identifier("var4"), Variable(1)))).eval
    //println(setMap)
    */