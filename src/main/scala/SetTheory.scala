import scala.collection.mutable

object SetTheory:
  val MapOfMaps: mutable.Map[String, mutable.Map[String,Any]] = mutable.Map[String, mutable.Map[String,Any]]()
  val macroMap: mutable.Map[String, ArithExp] = mutable.Map[String, ArithExp]()
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
    case Macro(op1: String, op2: ArithExp)
    case UseMacro(op1: String)
    val bindingScoping: Map[String, Int] = Map("x"->2, "Adan"->10)

    private def getExistingSets(op1: Identifier, op2: Identifier): (Set[(String, Any)], Set[(String, Any)]) =
      val newMap = mutable.Map[String,Any]()
      val firstSetName = op1.eval.asInstanceOf[String]
      val secondSetName = op2.eval.asInstanceOf[String]
      if(!MapOfMaps.contains(firstSetName)) {
        throw new RuntimeException("First set not found")
      }
      if(!MapOfMaps.contains(secondSetName)) {
        throw new RuntimeException("Second set not found")
      }
      val firstSet = MapOfMaps(firstSetName).toSet
      val secondSet = MapOfMaps(secondSetName).toSet
      (firstSet, secondSet)

    def eval: Any =
      this match{
        case Variable(obj) => obj
        case Insert(op1:Identifier, op2:Any) => (op1.eval, op2.eval)
        //Inserts op2 into set name found in MapOfMaps
        case Assign(op1, op2) =>
          val setName = op1.eval.asInstanceOf[String]
          val tupleAsAny = op2.eval
          def function(msg: Any): (String, Any) = {
            msg match {
              case (a: String, b: Any) => (a, b)
            }
          }
          val tuple = function(tupleAsAny)
          if(!MapOfMaps.contains(setName)){
            MapOfMaps += (setName -> mutable.Map[String,Any](tuple._1 -> tuple._2))
          }
          else {
            val set = MapOfMaps(setName)
            if(set.contains(tuple._1)){
              //Fail insertion
            }
            else {
              set += (tuple._1 -> tuple._2)
            }
          }
        case Check(op1, op2) =>
          val setName = op1.eval.asInstanceOf[String]
          val objectName = op2.eval.asInstanceOf[String]
          if(!MapOfMaps.contains(setName)){
            "Set " + setName + " does not contain " + objectName + "."
          }
          else {
            val set = MapOfMaps(setName)
            if(set.contains(objectName))
              "Set " + setName + " does contain " + objectName + "."
            else {
              "Set " + setName + " does not contain " + objectName + "."
            }
          }

        case Identifier(name) => name

        case Delete(op1: Identifier, op2: Identifier) =>
          val setName = op1.eval.asInstanceOf[String]
          val objectName = op2.eval.asInstanceOf[String]
          if(!MapOfMaps.contains(setName)) {
            return "Set " + setName + " does not exist. "
          }
          else {
            val set = MapOfMaps(setName)
            if(!set.contains(objectName))
              return "Object " + objectName + " does not exist inside " + setName + "."
          }
          val set = MapOfMaps(setName)
          set -= objectName
          "Successful deletion of " + objectName + " from " + setName + "."

        case Union(op1, op2) =>
          val setTuple = getExistingSets(op1,op2)
          val unionSet = setTuple._1.union(setTuple._2)
          val immutableUnionMap = unionSet.toMap
          val newUnionMap = mutable.Map() ++ immutableUnionMap
          newUnionMap

        case Intersection(op1, op2) =>
          val setTuple = getExistingSets(op1,op2)
          val intersectionSet = setTuple._1.intersect(setTuple._2)
          val immutableIntersectionMap = intersectionSet.toMap
          val newIntersectionMap = mutable.Map() ++ immutableIntersectionMap
          newIntersectionMap

        case Symmetric(op1, op2) =>
          val setTuple = getExistingSets(op1,op2)
          val symmetricSet = setTuple._1.diff(setTuple._2) union setTuple._2.diff(setTuple._1)
          val immutableIntersectionMap = symmetricSet.toMap
          val newSymmetricMap = mutable.Map() ++ immutableIntersectionMap
          newSymmetricMap

        case Difference(op1, op2) =>
          val setTuple = getExistingSets(op1,op2)
          val differenceSet = setTuple._1.diff(setTuple._2)
          val immutableDifferenceMap = differenceSet.toMap
          val newDifferenceMap = mutable.Map() ++ immutableDifferenceMap
          newDifferenceMap

        case Product(op1, op2) =>
          val secondSetName = op2.eval.asInstanceOf[String]
          val firstSetName = op1.eval.asInstanceOf[String]
          val cartesianMap = mutable.Map[String,Any]()
          getExistingSets(op1, op2)
          for((k,v) <- MapOfMaps(firstSetName)) {
            for((keys,values) <- MapOfMaps(secondSetName))
              cartesianMap += (k+keys -> (v, values))
          }
          cartesianMap

        case Macro(op1, op2) =>
          macroMap += (op1 -> op2)

        case UseMacro(op1) =>
          if(macroMap.contains(op1)) {
            macroMap(op1).eval
          }
          else{
            throw new RuntimeException("Macro doesn't exist")
          }
      }

  @main def createSetTheoryInputSession(): Unit =
    import ArithExp.*

    val testAssign = Assign(Identifier("Name1"), Insert(Identifier("key1"), Variable(1))).eval
    val testAssign3 = Assign(Identifier("Name1"), Insert(Identifier("key11"), Variable(11))).eval
    val testAssign2 = Assign(Identifier("Name2"), Insert(Identifier("key2"), Variable(2))).eval
    val testAssign4 = Assign(Identifier("Name2"), Insert(Identifier("key22"), Variable(22))).eval
    //println(testAssign)
    //val testDelete = Delete(Identifier("Name1"), Identifier("key")).eval2
    //println(testDelete)
    val testUnion = Union(Identifier("Name1"), Identifier("Name2")).eval
    println(testUnion)
    val testCartesianProduct = Product(Identifier("Name1"), Identifier("Name2")).eval
    println(testCartesianProduct)

    Macro("a", Assign(Identifier("Name10"), Insert(Identifier("key10"), Variable(1)))).eval
    val MacroResult = UseMacro("a").eval
    println(MacroResult)




