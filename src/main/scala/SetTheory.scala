object SetTheory:
  type BasicType = Int
  val MapOfMaps =  scala.collection.mutable.Map[String,scala.collection.mutable.Map[String,Any]]()
  enum ArithExp:
    case Value(input: BasicType)
    case Variable(obj: Any)
    case Identifier(name: String)
    case Add(op1: ArithExp, op2: ArithExp)
    case Sub(op1: ArithExp, op2: ArithExp)
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
    val bindingScoping: Map[String, Int] = Map("x"->2, "Adan"->10)

    def eval3: (String, Any) =
      this match {
        //Creates key:value pair for insertion into set
        case Insert(op1, op2) => (op1.eval2, op2.eval)
      }

    def eval2: String =
      this match {
        case Check(op1, op2) =>
          val setName = op1.eval2
          val objectName = op2.eval2
          if(!MapOfMaps.contains(setName)){
            return "Set " + setName + " does not contain " + objectName + "."
          }
          else {
            val set = MapOfMaps(setName)
            if(set.contains(objectName))
              return "Set " + setName + " does contain " + objectName + "."
            else {
              return "Set " + setName + " does not contain " + objectName + "."
            }
          }
        case Identifier(name) => (name)
        //Inserts op2 into set name found in MapOfMaps
        case Assign(op1, op2) =>
          val setName = op1.eval2
          val tuple = op2.eval3
          if(!MapOfMaps.contains(setName)){
            MapOfMaps += (setName -> scala.collection.mutable.Map[String,Any](tuple._1 -> tuple._2))
          }
          else {
            val set = MapOfMaps(setName)
            if(set.contains(tuple._1))
              return "Insertion failed. " + tuple._1 + " is already in " + setName + "."
            else {
              set += (tuple)
            }
          }
          return "Successful insertion of " + tuple._1 + " into " + setName + "."
        case Delete(op1: Identifier, op2: Identifier) =>
          val setName = op1.eval2
          val objectName = op2.eval2
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
      }

    def evalCartesianProduct: scala.collection.mutable.Map[String, Any] =
      this match{
        case Product(op1, op2) =>
          val cartesianMap = scala.collection.mutable.Map[String,Any]()
          val firstSetName = op1.eval2
          val secondSetName = op2.eval2
          if(!MapOfMaps.contains(firstSetName)) {
            if(MapOfMaps.contains(secondSetName))
              return MapOfMaps(secondSetName)
            else
              return cartesianMap
          }
          if(!MapOfMaps.contains(secondSetName)) {
            if(MapOfMaps.contains(firstSetName))
              return MapOfMaps(firstSetName)
            else
              return cartesianMap
          }
          for((k,v) <- MapOfMaps(firstSetName)) {
            for((keys,values) <- MapOfMaps(secondSetName))
              cartesianMap += (k+keys -> (v, values))
          }
          return cartesianMap
      }

    def evalUnion: scala.collection.mutable.Map[String, Any] =
      this match{
        case Union(op1, op2) =>
          val unionMap = scala.collection.mutable.Map[String,Any]()
          val firstSetName = op1.eval2
          val secondSetName = op2.eval2
          if(!MapOfMaps.contains(firstSetName)) {
            if(MapOfMaps.contains(secondSetName))
              return MapOfMaps(secondSetName)
            else
              return unionMap
          }
          if(!MapOfMaps.contains(secondSetName)) {
            if(MapOfMaps.contains(firstSetName))
              return MapOfMaps(firstSetName)
            else
              return unionMap
          }
          val firstSet = MapOfMaps(firstSetName).toSet
          val secondSet = MapOfMaps(secondSetName).toSet
          val unionSet = firstSet.union(secondSet)
          val unmutableUnionMap = unionSet.toMap
          val newUnionMap = scala.collection.mutable.Map() ++ unmutableUnionMap
          return newUnionMap
      }

    def evalIntersection: scala.collection.mutable.Map[String, Any] =
      this match{
        case Intersection(op1, op2) =>
          val intersectionMap = scala.collection.mutable.Map[String,Any]()
          val firstSetName = op1.eval2
          val secondSetName = op2.eval2
          if(!MapOfMaps.contains(firstSetName)) {
            if(MapOfMaps.contains(secondSetName))
              return MapOfMaps(secondSetName)
            else
              return intersectionMap
          }
          if(!MapOfMaps.contains(secondSetName)) {
            if(MapOfMaps.contains(firstSetName))
              return MapOfMaps(firstSetName)
            else
              return intersectionMap
          }
          val firstSet = MapOfMaps(firstSetName).toSet
          val secondSet = MapOfMaps(secondSetName).toSet
          val intersectionSet = firstSet.intersect(secondSet)
          val unmutableIntersectionMap = intersectionSet.toMap
          val newIntersectionMap = scala.collection.mutable.Map() ++ unmutableIntersectionMap
          return newIntersectionMap
      }

    def evalDifference: scala.collection.mutable.Map[String, Any] =
      this match{
        case Difference(op1, op2) =>
          val differenceMap = scala.collection.mutable.Map[String,Any]()
          val firstSetName = op1.eval2
          val secondSetName = op2.eval2
          if(!MapOfMaps.contains(firstSetName)) {
            if(MapOfMaps.contains(secondSetName))
              return MapOfMaps(secondSetName)
            else
              return differenceMap
          }
          if(!MapOfMaps.contains(secondSetName)) {
            if(MapOfMaps.contains(firstSetName))
              return MapOfMaps(firstSetName)
            else
              return differenceMap
          }
          val firstSet = MapOfMaps(firstSetName).toSet
          val secondSet = MapOfMaps(secondSetName).toSet
          val differenceSet = firstSet.diff(secondSet)
          val unmutableIntersectionMap = differenceSet.toMap
          val newIntersectionMap = scala.collection.mutable.Map() ++ unmutableIntersectionMap
          return newIntersectionMap
      }

    def evalSymmetric: scala.collection.mutable.Map[String, Any] =
      this match{
        case Symmetric(op1, op2) =>
          val symmetricMap = scala.collection.mutable.Map[String,Any]()
          val firstSetName = op1.eval2
          val secondSetName = op2.eval2
          if(!MapOfMaps.contains(firstSetName)) {
            if(MapOfMaps.contains(secondSetName))
              return MapOfMaps(secondSetName)
            else
              return symmetricMap
          }
          if(!MapOfMaps.contains(secondSetName)) {
            if(MapOfMaps.contains(firstSetName))
              return MapOfMaps(firstSetName)
            else
              return symmetricMap
          }
          val firstSet = MapOfMaps(firstSetName).toSet
          val secondSet = MapOfMaps(secondSetName).toSet
          val symmetricSet = (firstSet.diff(secondSet)) union (secondSet.diff(firstSet))
          val unmutableIntersectionMap = symmetricSet.toMap
          val newSymmetricMap = scala.collection.mutable.Map() ++ unmutableIntersectionMap
          return newSymmetricMap
      }

    def eval1: BasicType =
      this match {
        case Value(input) => input
        case Identifier(name) => bindingScoping(name)
        case Add(op1, op2) => op1.eval1 + op2.eval1
        case Sub(op1, op2) => op1.eval1 - op2.eval1
      }
      
    def eval: Any =
      this match{
        case Variable(obj) => obj
      }

  @main def createSetTheoryInputSession(): Unit =
    import ArithExp.*
    import scala.util.control.Breaks._
    val firstExpression = Sub(Add(Add(Value(2), Value(3)),Identifier("Adan")), Identifier("x")).eval1
    println(firstExpression)

    val hello = Add(Value(5),Value(6)).eval1
    println(hello)
    val testAssign = Assign(Identifier("Name1"), Insert(Identifier("key1"), Variable(1))).eval2
    val testAssign3 = Assign(Identifier("Name1"), Insert(Identifier("key11"), Variable(11))).eval2
    val testAssign2 = Assign(Identifier("Name2"), Insert(Identifier("key2"), Variable(2))).eval2
    val testAssign4 = Assign(Identifier("Name2"), Insert(Identifier("key22"), Variable(22))).eval2
    //println(testAssign)
    //val testDelete = Delete(Identifier("Name1"), Identifier("key")).eval2
    //println(testDelete)
    val testUnion = Union(Identifier("Name1"), Identifier("Name2")).evalUnion
    println(testUnion)
    val testCartesianProduct = Product(Identifier("Name1"), Identifier("Name2")).evalCartesianProduct
    println(testCartesianProduct)

/*
val scanner = java.util.Scanner(System.in)
breakable {
  while (true) {
    val expression = scanner.nextLine()
    if (expression.contains("stop"))
      break;
    expression.eval
  }
}
*/


