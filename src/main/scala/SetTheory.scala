//Cristian Trandafir

import SetTheory.AccessModifier.{Private, Protected, Public}
import SetTheory.ArithExp.{ClassDef, Constructor, Field, FieldAssign, InterfaceDecl, InvokeMethod, Method, NewObject, ThrowException}
import sun.security.ec.point.ProjectivePoint.Mutable

import scala.annotation.tailrec
import scala.collection.mutable

object SetTheory:
  val scopeWithExceptionMap: mutable.Map[String, mutable.Map[String, Array[ArithExp]]] = mutable.Map[String, mutable.Map[String, Array[ArithExp]]]("main" -> mutable.Map[String,Array[ArithExp]]())
  //"scopeName" -> Map("exceptionName1" -> Array[ArithExpCommands]),
  //                  ("exceptionName2" -> Array[ArithExpCommands])
  //"scopeName2" -> Map("exceptionName4" -> Array[ArithExpCommands])
  val exceptionMap: mutable.Map[String, Any] = mutable.Map[String, Any]()
  val interfaceMap: mutable.Map[String, Any] = mutable.Map[String, Any]()
  //"interface" -> Map("name" -> "interfaceName"
  //                   "parents" -> Array[parentClassNames] immediate parent first
  //                   "fields" -> Map("private" -> Map("fieldName" -> Any)
  //                                   "public" -> Map("fieldName" -> Any)
  //                                   "protected" -> Map("fieldName" -> Any)
  //                   "methods" -> Map("protected" -> Map("methodName" -> Array[ArithExp])
  //                                    "private" -> Map("methodName" -> Array[ArithExp])
  //                                    "private" -> Map("methodName" -> Array[ArithExp])
  //                                    "abstract" -> Map("methodName" -> Array[ArithExp])
  //                   "innerC" -> Map("className" -> class)
  //                   "innerI" -> Map("interfaceName" -> interface)
  val objectMap: mutable.Map[String, Any] = mutable.Map[String, Any]()
  //objectName -> objectInstantiation
  val classMap: mutable.Map[String, Any] = mutable.Map[String, Any]()
  //"className" -> Map("name" -> "className"
  //                   "constructor" -> Array[FieldAssign("fieldName",Any])
  //                   "parents" -> Array[parentClassNames] immediate parent first
  //                   "fields" -> Map("private" -> Map("fieldName" -> Any)
  //                                   "public" -> Map("fieldName" -> Any)
  //                                   "protected" -> Map("fieldName" -> Any)
  //                   "methods" -> Map("protected" -> Map("methodName" -> Array[ArithExp])
  //                                    "private" -> Map("methodName" -> Array[ArithExp])
  //                                    "public" -> Map("methodName" -> Array[ArithExp])
  //                                    "abstract" -> Map("methodName" -> Array[ArithExp])
  //                   "innerC" -> Map("className" -> class)
  //                   "innerI" -> Map("interfaceName" -> interface)
  val setMap: mutable.Map[String, Any] = mutable.Map[String, Any]("main" -> mutable.Map[String, Any]())
  private val macroMap: mutable.Map[String, ArithExp] = mutable.Map[String, ArithExp]()
  //main.scope1 -> main
  private val scopeMap: mutable.Map[String, String] = mutable.Map[String, String]("main" -> "main")
  //current -> main.scope1
  private val currentScope: mutable.Map[String, String] = mutable.Map[String, String]("current" -> "main")
  //"current" -> map
  private val currentMap: mutable.Map[String, Any] = mutable.Map[String, Any]("current" -> setMap)

  def IF(condition: => Boolean, thenClause: => Any, elseClause: => Any): Any =
      if (condition)
        thenClause
      else
        elseClause

  enum AccessModifier:
    case Private()
    case Public()
    case Protected()
    case Abstract()

    def eval: String =
      this match {
        case Private() =>
          "private"
        case Public() =>
          "public"
        case Protected() =>
          "protected"
        case Abstract() =>
          "abstract"
      }

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
    case Scope(newScope: Identifier, parentScope: Identifier, command: ArithExp, exception: Any)
    //Class
    case NewObject(className: String, variableName: String)
    case FieldAssign(fieldName: String, obj: Any)
    case Method(methodName: String, access: AccessModifier, commands: Array[ArithExp])
    case Constructor(commands: Array[FieldAssign])
    case Field(fieldName: String, access: AccessModifier)
    case ClassDef(className: String, fields: Array[Field], constructor: Constructor, methods: Array[Method], nestedC: Any, nestedI: Any)
    case InvokeMethod(objectName: String, methodName: String)
    //Abstract Classes and Interfaces
    case AbstractClassDef(className: String, fields: Array[Field], constructor: Constructor, methods: Array[Method], nestedC: Any, nestedI: Any)
    case InterfaceDecl(interfaceName: String, fields: Array[Field], methods: Array[Method], nestedC: Any, nestedI: Any)
    //Exception Handling
    case CatchException(exceptionName: String, tryBlock: Array[ArithExp], catchBlock: Array[ArithExp])
    case ExceptionClassDef(exceptionName: String, field: String)
    case ThrowException(exceptionName: String)


    //Helper method that returns a tuple containing 2 sets to the set operation functions in eval
    private def getExistingSets(setName1: Identifier, setName2: Identifier): (Set[(String, Any)], Set[(String, Any)]) =
      val firstSetName = setName1.eval.asInstanceOf[String]
      val secondSetName = setName2.eval.asInstanceOf[String]
      val a = scopeMap(currentScope("current"))
      val inScopeMap = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), firstSetName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
      if (!inScopeMap.contains(firstSetName))
        throw new RuntimeException("First set not found")
      val inScopeMap2 = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), secondSetName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
      if (!inScopeMap2.contains(secondSetName))
        throw new RuntimeException("Second set not found")
      val firstSet = inScopeMap(firstSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      val secondSet = inScopeMap2(secondSetName).asInstanceOf[mutable.Map[String, Any]].toSet
      (firstSet, secondSet)

    //Starts from main and traverses setMap to find specified scope in scopeHierarchyString
    @tailrec
    private def recursiveResolveScope(map: mutable.Map[String, Any], scopeHierarchyString: String): mutable.Map[String, Any] =
    //If scope string still has "." delimiter - advance the scope from main.scope1 -> scope1
      if (scopeHierarchyString.contains(".")) {
        //main.scope1.scope2 would turn to scope1.scope2
        val reducedString = scopeHierarchyString.substring(scopeHierarchyString.indexOf(".") + 1, scopeHierarchyString.length)
        //main.scope1.scope would turn to main
        val nextScope = scopeHierarchyString.substring(0, scopeHierarchyString.indexOf("."))
        //This command would be main(reducedString) which returns the scope1Map that contains scope2Map
        if (!map.contains(nextScope)) {
          throw new RuntimeException("One of your scopes does not exist." + nextScope)
        }
        val newMap = map(nextScope).asInstanceOf[mutable.Map[String, Any]]
        //Recurse until you get to .scope2, or whatever the last scope is
        recursiveResolveScope(newMap, reducedString)
      }
      //If scope string has no "." delimiter - this is the last scope.
      else {
        //No past usages found so just return current scope
        if (!map.contains(scopeHierarchyString)) {
          currentMap("current").asInstanceOf[mutable.Map[String, Any]]
        }
        //Found
        else
          map(scopeHierarchyString).asInstanceOf[mutable.Map[String, Any]]
      }

    //Starts from current scope and searches for the string through the parent scopes of current scope. Returns current scope if searchString is not found.
    @tailrec
    private def resolveScopeToMain(map: mutable.Map[String, Any], scopeHierarchyString: String, searchString: String, highestLevelMap: mutable.Map[String, Any]): mutable.Map[String, Any] =
    //If found in current scope
      if (map.contains(searchString))
        map
      //If not found in current scope, recurse towards main.
      else {
        //We are in main scope, else in some subscope
        if (!scopeHierarchyString.contains("."))
          return highestLevelMap
        //main.scope1.scope2 would turn to main.scope1
        val reducedString = scopeHierarchyString.substring(0, scopeHierarchyString.lastIndexOf("."))
        val firstString = scopeHierarchyString.substring(0, scopeHierarchyString.indexOf("."))
        //This makes newMap equal to the parent Map the current Map is stored in
        val newMap = recursiveResolveScope(setMap, reducedString)
        resolveScopeToMain(newMap, reducedString, searchString, highestLevelMap)
      }

    infix def Implements(interfaceName: String): Unit =
      if (!this.isInstanceOf[ClassDef])
        if (!this.isInstanceOf[AbstractClassDef])
          throw new RuntimeException("Only classes can implement interfaces")
      if (!interfaceMap.contains(interfaceName))
        throw new RuntimeException("The interface you want to extend does not exist")
      val currentName = this.eval.asInstanceOf[String]
      //Prepend parent name to parent array
      val currentBlueprintMap = classMap(currentName).asInstanceOf[mutable.Map[String, Any]]
      val parentBlueprintMap = interfaceMap(interfaceName).asInstanceOf[mutable.Map[String, Any]]
      currentBlueprintMap("parents") = interfaceName +: parentBlueprintMap("parents").asInstanceOf[Array[String]]
      //Override parent's abstract methods
      val currentAbstractMap = currentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
      val parentAbstractMap = parentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
      for (method <- parentAbstractMap)
      //Add any unimplemented methods from parent class into child class
        if (!currentAbstractMap.contains(method._1))
          currentAbstractMap += (method._1 -> method._2)
      for (method <- currentAbstractMap)
        if (!parentAbstractMap.contains(method._1))
          throw new RuntimeException("Cannot override abstract method that doesn't exist in parent classes.")

    //The newly declared class to the left inherits from the String name of the already existing class to the right
    infix def Extends(parentName: String): Unit =
      this match {
        case classDef: ArithExp.ClassDef =>

          if (!classMap.contains(parentName))
            throw new RuntimeException("The class you want to extend does not exist")
          //.eval runs ClassDef which defines child class and returns its string name
          val currentName = this.eval.asInstanceOf[String]
          if (currentName == parentName)
            throw new RuntimeException("Cannot inherit from own class.")
          //Prepend parent name to parent array
          val currentBlueprintMap = classMap(currentName).asInstanceOf[mutable.Map[String, Any]]
          val parentBlueprintMap = classMap(parentName).asInstanceOf[mutable.Map[String, Any]]
          currentBlueprintMap("parents") = parentName +: parentBlueprintMap("parents").asInstanceOf[Array[String]]
          //Override parent's abstract methods
          val currentAbstractMap = currentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          val parentAbstractMap = parentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          for (method <- parentAbstractMap)
          //Add any unimplemented methods from parent class into child class
            if (!currentAbstractMap.contains(method._1))
              currentAbstractMap += (method._1 -> method._2)
          for (method <- currentAbstractMap)
            if (!parentAbstractMap.contains(method._1))
              throw new RuntimeException("Cannot override abstract method that doesn't exist in parent classes.")

        case abstractClassDef: ArithExp.AbstractClassDef =>

          if (!classMap.contains(parentName))
            throw new RuntimeException("The class you want to extend does not exist")
          //.eval runs ClassDef which defines child class and returns its string name
          val currentName = this.eval.asInstanceOf[String]
          if (currentName == parentName)
            throw new RuntimeException("Cannot inherit from own class.")
          //Prepend parent name to parent array
          val currentBlueprintMap = classMap(currentName).asInstanceOf[mutable.Map[String, Any]]
          val parentBlueprintMap = classMap(parentName).asInstanceOf[mutable.Map[String, Any]]
          currentBlueprintMap("parents") = parentName +: parentBlueprintMap("parents").asInstanceOf[Array[String]]
          //Override parent's abstract methods
          val currentAbstractMap = currentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          val parentAbstractMap = parentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          for (method <- parentAbstractMap)
          //Add any unimplemented methods from parent class into child class
            if (!currentAbstractMap.contains(method._1))
              currentAbstractMap += (method._1 -> method._2)
          for (method <- currentAbstractMap)
            if (!parentAbstractMap.contains(method._1))
              throw new RuntimeException("Cannot override abstract method that doesn't exist in parent classes.")

        case interfaceDecl: ArithExp.InterfaceDecl =>

          if (!interfaceMap.contains(parentName))
            throw new RuntimeException("The interface you want to extend does not exist")
          //.eval runs ClassDef which defines child class and returns its string name
          val currentName = this.eval.asInstanceOf[String]
          //Prepend parent name to parent array
          val currentBlueprintMap = interfaceMap(currentName).asInstanceOf[mutable.Map[String, Any]]
          val parentBlueprintMap = interfaceMap(parentName).asInstanceOf[mutable.Map[String, Any]]
          currentBlueprintMap("parents") = parentName +: parentBlueprintMap("parents").asInstanceOf[Array[String]]
          //Override parent's abstract methods
          val currentAbstractMap = currentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          val parentAbstractMap = parentBlueprintMap("methods").asInstanceOf[mutable.Map[String, mutable.Map[String, Array[ArithExp]]]]("abstract")
          for (method <- parentAbstractMap)
          //Add any unimplemented methods from parent class into child class
            if (!currentAbstractMap.contains(method._1))
              currentAbstractMap += (method._1 -> method._2)

        case _ =>
          throw new RuntimeException("Only classes and interfaces can use Extends.")
      }


    def eval: Any =
      this match {
        case Scope(newScope: Identifier, parentScope: Identifier, command: ArithExp, exception: Any) =>
          //Parent scope does not exist
          if (!scopeMap.contains(parentScope.eval.asInstanceOf[String]))
            throw new RuntimeException("Specified scope does not exist.")
          //'.' is delimiter for Strings that keep track of scope, don't include
          if (newScope.eval.asInstanceOf[String].contains(".")) {
            if (!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String]))
              throw new RuntimeException("Please do not include '.' in your scope name.")
          }
          //Not creating new scope, just editing specified one
          if (!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String])) {
            //Include parentScope.newScope in map pointing to parentScope
            scopeMap += (parentScope.eval.asInstanceOf[String] + "." + newScope.eval.asInstanceOf[String] -> parentScope.eval.asInstanceOf[String])
          }
          //Assign class variables
          currentMap("current") = recursiveResolveScope(setMap, parentScope.eval.asInstanceOf[String] + "." + newScope.eval.asInstanceOf[String])
          if (!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String]))
            currentScope("current") = parentScope.eval.asInstanceOf[String] + "." + newScope.eval.asInstanceOf[String]
          val returnArithExp = command.eval

          currentMap("current") = recursiveResolveScope(setMap, parentScope.eval.asInstanceOf[String] + "." + newScope.eval.asInstanceOf[String])
          if (!newScope.eval.asInstanceOf[String].equals(parentScope.eval.asInstanceOf[String]))
            currentScope("current") = parentScope.eval.asInstanceOf[String] + "." + newScope.eval.asInstanceOf[String]

          exception match {
            case catchException: ArithExp.CatchException =>
              catchException.eval
            case _ =>
              //Do nothing because no try/catch was passed into method
          }

          //Reset so ArithExp commands without Scope in them don't get messed up.
          currentMap("current") = setMap
          currentScope("current") = "main"
          returnArithExp

        //Just a wrapper method that returns the object passed in
        case Variable(obj) => obj

        //Returns (String, Any) tuple for insertion
        case Insert(op1: Identifier, op2: Any) => (op1.eval, op2.eval)

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
          val fullScope = currentScope("current") //main.scope1 -> main.scope1
          //reducedScopeMap
          if (currentMap("current").equals(setMap)) {
            currentMap("current") = setMap("main")
          }
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], fullScope, setName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          //main.scope1 -> scope1
          if (!fullScope.equals("main")) {
            val onlyNewScope = fullScope.substring(fullScope.lastIndexOf(".") + 1, fullScope.length)
            //If there's already a scope or set with this name, error out
            if (currentMap("current").asInstanceOf[mutable.Map[String, Any]].contains(onlyNewScope))
              throw new RuntimeException("There is already a set or scope with this name.\nIf you want to access a scope and not create a new one, type the same scope name for both fields.")
            //Insert new map of maps into scope for (set name -> (obj name -> obj))
            if (!mapInScope.contains(onlyNewScope)) {
              mapInScope += (onlyNewScope -> mutable.Map[String, mutable.Map[String, Any]](setName -> mutable.Map[String, Any](stringAnyTuple._1 -> stringAnyTuple._2)))
            }
            mapInScope
          }
          //Main special case
          else {
            val onlyNewScope = fullScope
            if (!mapInScope.contains(setName)) {
              mapInScope += (setName -> mutable.Map[String, Any](stringAnyTuple._1 -> stringAnyTuple._2))
            }
            else {
              val set = mapInScope(setName).asInstanceOf[mutable.Map[String, Any]]
              if (set.contains(stringAnyTuple._1)) {
                throw new RuntimeException("Failed to insert duplicate value " + stringAnyTuple._1 + " into set " + setName + ".")
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
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), setName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          if (!mapInScope.contains(setName)) {
            "Set " + setName + " does not exist."
          }
          else {
            val set = mapInScope(setName).asInstanceOf[mutable.Map[String, mutable.Map[String, Any]]]
            if (set.contains(objectName))
              "Set " + setName + " does contain " + objectName + "."
            else {
              "Set " + setName + " does not contain " + objectName + "."
            }
          }

        //Just a wrapper method that returns the name as a string
        case Identifier(name: String) => name

        //Deletes objectIdentifier from setIdentifier
        case Delete(setIdentifier: Identifier, objectIdentifier: Identifier) =>
          val setName = setIdentifier.eval.asInstanceOf[String]
          val objectName = objectIdentifier.eval.asInstanceOf[String]
          val mapInScope = resolveScopeToMain(currentMap("current").asInstanceOf[mutable.Map[String, Any]], currentScope("current"), setName, currentMap("current").asInstanceOf[mutable.Map[String, Any]])
          if (!mapInScope.contains(setName)) {
            return "Set " + setName + " does not exist. "
          }
          else {
            val set = mapInScope(setName).asInstanceOf[mutable.Map[String, mutable.Map[String, Any]]]
            if (!set.contains(objectName))
              return "Object " + objectName + " does not exist inside " + setName + "."
          }
          val set = mapInScope(setName).asInstanceOf[mutable.Map[String, mutable.Map[String, Any]]]
          set -= objectName
          "Successful deletion of " + objectName + " from " + setName + "."

        //Union of 2 sets
        case Union(setName1: Identifier, setName2: Identifier) =>
          val setTuple = getExistingSets(setName1, setName2)
          val unionSet = setTuple._1.union(setTuple._2)
          val immutableUnionMap = unionSet.toMap
          val newUnionMap = mutable.Map() ++ immutableUnionMap
          newUnionMap

        //Intersection of 2 sets
        case Intersection(setName1: Identifier, setName2: Identifier) =>
          val setTuple = getExistingSets(setName1, setName2)
          val intersectionSet = setTuple._1.intersect(setTuple._2)
          val immutableIntersectionMap = intersectionSet.toMap
          val newIntersectionMap = mutable.Map() ++ immutableIntersectionMap
          newIntersectionMap

        //Symmetric Difference of 2 sets
        case Symmetric(setName1: Identifier, setName2: Identifier) =>
          val setTuple = getExistingSets(setName1, setName2)
          val symmetricSet = setTuple._1.diff(setTuple._2) union setTuple._2.diff(setTuple._1)
          val immutableIntersectionMap = symmetricSet.toMap
          val newSymmetricMap = mutable.Map() ++ immutableIntersectionMap
          newSymmetricMap

        //Difference of 2 sets
        case Difference(setName1: Identifier, setName2: Identifier) =>
          val setTuple = getExistingSets(setName1, setName2)
          val differenceSet = setTuple._1.diff(setTuple._2)
          val immutableDifferenceMap = differenceSet.toMap
          val newDifferenceMap = mutable.Map() ++ immutableDifferenceMap
          newDifferenceMap

        //Cartesian Product of 2 sets
        case Product(setName1: Identifier, setName2: Identifier) =>
          val secondSetName = setName2.eval.asInstanceOf[String]
          val firstSetName = setName1.eval.asInstanceOf[String]
          val cartesianMap = mutable.Map[String, Any]()
          getExistingSets(setName1, setName2) //Checks if sets exist
          val firstSet = recursiveResolveScope(currentMap("current").asInstanceOf[mutable.Map[String, Any]], firstSetName)
          val secondSet = recursiveResolveScope(currentMap("current").asInstanceOf[mutable.Map[String, Any]], secondSetName)
          for ((k, v) <- firstSet) {
            for ((keys, values) <- secondSet)
              cartesianMap += (k + keys -> (v, values))
          }
          cartesianMap

        case Macro(name: Identifier, expression: ArithExp) =>
          //Overwrite macroMap
          if (macroMap.contains(name.eval.asInstanceOf[String]))
            return macroMap(name.eval.asInstanceOf[String]) = expression
          //New addition
          macroMap += (name.eval.asInstanceOf[String] -> expression)

        case UseMacro(name: Identifier) =>
          if (macroMap.contains(name.eval.asInstanceOf[String])) {
            macroMap(name.eval.asInstanceOf[String]).eval
          }
          else {
            throw new RuntimeException("Macro doesn't exist.")
          }

        //Class operations

        case ClassDef(className: String, fields: Array[Field], constructor: Constructor, methods: Array[Method], nestedC: Any, nestedI: Any) =>
          if (classMap.contains(className))
            throw new RuntimeException("Class " + className + " is already defined.")
          for (method <- methods) {
            if (method._2.eval == "abstract") {
              val arithArray = method._3.asInstanceOf[Array[ArithExp]]
              if (arithArray.isEmpty) {
                throw new RuntimeException("Cannot define abstract method in concrete class.")
              }
            }
          }
          classMap += recurseAddClassToMap(className, fields, constructor, methods, nestedC, nestedI)
          className

        case Field(fieldName: String, access: AccessModifier) =>

        case Constructor(commands: Array[FieldAssign]) =>
          commands

        case FieldAssign(fieldName: String, obj: Any) =>

        case Method(methodName: String, access: AccessModifier, commands: Array[ArithExp]) =>

        case NewObject(className: String, variableName: String) =>
          //Instantiate object by copying its class blueprint
          val classBlueprintMap = classMap(className).asInstanceOf[mutable.Map[String, mutable.Map[String, mutable.Map[String, Any]]]]
          //clone() is shallow copy so this sequence deep copies the class fields for each object instantiation.
          val objectBlueprintMap = classBlueprintMap.clone()
          val objectFieldMap = classBlueprintMap("fields").clone()
          objectBlueprintMap("fields") = objectFieldMap
          val objectPrivateFieldMap = objectFieldMap("private").clone()
          val objectPublicFieldMap = objectFieldMap("public").clone()
          val objectProtectedFieldMap = objectFieldMap("protected").clone()
          objectBlueprintMap("fields")("private") = objectPrivateFieldMap
          objectBlueprintMap("fields")("public") = objectPublicFieldMap
          objectBlueprintMap("fields")("protected") = objectProtectedFieldMap
          objectMap += (variableName -> objectBlueprintMap)
          //Necessary fields
          val classConstructorArray = classBlueprintMap("constructor").asInstanceOf[Array[FieldAssign]]
          val classPrivateFieldMap = classBlueprintMap("fields")("private")
          val classParentArray = objectBlueprintMap("parents").asInstanceOf[Array[String]]
          val objectAbstractMap = objectBlueprintMap("methods")("abstract")
          //If there is no parent class, simply run the commands in the current constructor
          if (classParentArray.isEmpty) {
            for (commands <- classConstructorArray) {
              if (!objectPrivateFieldMap.contains(commands._1)) {
                if (!objectPublicFieldMap.contains(commands._1)) {
                  if (!objectProtectedFieldMap.contains(commands._1))
                    throw new RuntimeException("Field does not exist.")
                  else
                    objectProtectedFieldMap(commands._1) = commands._2
                }
                else
                  objectPublicFieldMap(commands._1) = commands._2
              }
              else
                objectPrivateFieldMap(commands._1) = commands._2
            }
          }
          //Else parent class exists
          else {
            //Reverse iterate through parent classes starting from oldest ancestor
            val reverseIterator: Iterator[String] = classParentArray.reverseIterator
            //For each parent class
            reverseIterator.foreach(parentName =>
              val currentClassBlueprintMap = classMap(parentName).asInstanceOf[mutable.Map[String, Any]]
              val currentClassConstructorArray = currentClassBlueprintMap("constructor").asInstanceOf[Array[FieldAssign]]
              //Parent class's fields
              val currentClassFieldMap = currentClassBlueprintMap("fields").asInstanceOf[mutable.Map[String, mutable.Map[String, Any]]]
              val currentClassPrivateFieldMap = currentClassFieldMap("private")
              val currentClassPublicFieldMap = currentClassFieldMap("public")
              val currentClassProtectedFieldMap = currentClassFieldMap("protected")
              //This is the child class's map; add all of its parent's public and protected fields into it
              //This implementation causes shadowing of fields in parent classes.
              for (field <- currentClassPrivateFieldMap) {
                if (currentClassPrivateFieldMap.contains(field._1))
                  objectPrivateFieldMap(field._1) = field._2
                else
                  objectPrivateFieldMap += (field._1 -> field._2)
              }
              for (field <- currentClassPublicFieldMap) {
                if (objectPublicFieldMap.contains(field._1))
                  objectPublicFieldMap(field._1) = field._2
                else
                  objectPublicFieldMap += (field._1 -> field._2)
              }
              for (field <- currentClassProtectedFieldMap) {
                if (objectProtectedFieldMap.contains(field._1))
                  objectProtectedFieldMap(field._1) = field._2
                else
                  objectProtectedFieldMap += (field._1 -> field._2)
              }
              //Then run current constructor;
              for (commands <- currentClassConstructorArray) {
                if (!objectPrivateFieldMap.contains(commands._1)) {
                  if (!objectPublicFieldMap.contains(commands._1)) {
                    if (!objectProtectedFieldMap.contains(commands._1))
                      throw new RuntimeException("Field does not exist.")
                    else
                      objectProtectedFieldMap(commands._1) = commands._2
                  }
                  else
                    objectPublicFieldMap(commands._1) = commands._2
                }
                //Check if current class defines this private field. If not, error.
                else {
                  if (currentClassPrivateFieldMap.contains(commands._1))
                    objectPrivateFieldMap(commands._1) = commands._2
                  else
                    throw new RuntimeException("Tried to access private parent field")
                }
              }
              //Then iterate to next oldest parent class
            )
            //Child class
            for (commands <- classConstructorArray) {
              if (!objectPrivateFieldMap.contains(commands._1)) {
                if (!objectPublicFieldMap.contains(commands._1)) {
                  if (!objectProtectedFieldMap.contains(commands._1)) {
                    throw new RuntimeException("Field does not exist.")
                  }
                  else
                    objectProtectedFieldMap(commands._1) = commands._2
                }
                else
                  objectPublicFieldMap(commands._1) = commands._2
              }
              //Check if child object has private field in class def. If not, illegal access to private parent field.
              else if (classPrivateFieldMap.contains(commands._1))
                objectPrivateFieldMap(commands._1) = commands._2
              else
                throw new RuntimeException("Tried to access private parent field")
            }
          }
          for (method <- objectAbstractMap) {
            if (method._2.asInstanceOf[Array[ArithExp]].isEmpty)
              throw new RuntimeException("Can't instantiate class that doesn't implement all abstract methods")
          }


        //Invokes an object's method
        case InvokeMethod(objectName: String, methodName: String) =>
          if (!objectMap.contains(objectName))
            throw new RuntimeException("Object does not exist")
          val objectBlueprintMap = objectMap(objectName).asInstanceOf[mutable.Map[String, Any]]
          val objectMethodMap = objectBlueprintMap("methods").asInstanceOf[mutable.Map[String, Any]]
          val objectPrivateMethodMap = objectMethodMap("private").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
          val objectPublicMethodMap = objectMethodMap("public").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
          val objectProtectedMethodMap = objectMethodMap("protected").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
          val objectAbstractMethodMap = objectMethodMap("abstract").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
          if (!checkCurrentClassForMethod(objectAbstractMethodMap, methodName)) {
            if (!checkCurrentClassForMethod(objectPublicMethodMap, methodName)) {
              if (!checkCurrentClassForMethod(objectPrivateMethodMap, methodName)) {
                if (!checkCurrentClassForMethod(objectProtectedMethodMap, methodName)) {
                  //Not found in current class scope, start checking parents
                  val objectParentArray = objectBlueprintMap("parents").asInstanceOf[Array[String]]
                  if (!objectParentArray.isEmpty) {
                    if (!recurseClassHierarchy(objectParentArray(0), methodName))
                      throw new RuntimeException("Method not found")
                  }
                  //Current class has no parents
                  else {
                    throw new RuntimeException("Method not found")
                  }
                }
              }
            }
          }
        //If made it here, return global variable of last method

        //Interface and Abstract Class

        case AbstractClassDef(className: String, fields: Array[Field], constructor: Constructor, methods: Array[Method], nestedC: Any, nestedI: Any) =>
          if (classMap.contains(className))
            throw new RuntimeException("Class " + className + " is already defined.")
          classMap += recurseAddClassToMap(className, fields, constructor, methods, nestedC, nestedI)
          className

        case InterfaceDecl(interfaceName: String, fields: Array[Field], methods: Array[Method], nestedC: Any, nestedI: Any) =>
          if (interfaceMap.contains(interfaceName))
            throw new RuntimeException("Interface " + interfaceName + " is already defined.")
          interfaceMap += recurseAddInterfaceToMap(interfaceName, fields, methods, nestedC, nestedI)
          interfaceName

        case ExceptionClassDef(exceptionName: String, field: String) =>
            if (exceptionMap.contains(exceptionName))
              throw new RuntimeException("Can't define 2 exceptions with the same name")
            else
              exceptionMap.addOne(exceptionName, field)

        case ThrowException(exceptionName: String) =>
          if(!exceptionMap.contains(exceptionName))
            throw new RuntimeException("Can't throw exception that is undefined.")
          else
            recurseCheckForException(exceptionName, currentScope("current"))

        case CatchException(exceptionName: String, tryBlock: Array[ArithExp], catchBlock: Array[ArithExp]) =>
          scopeWithExceptionMap += (currentScope("current") -> mutable.Map(exceptionName -> catchBlock))
          tryBlock.foreach { command =>
              command.eval
          }

      }

    //Helper method that recursively searches for a catch block in the scopeMap matching the exceptionName passed in
    @tailrec
    private def recurseCheckForException(exceptionName: String, scopeHierarchyString: String): Unit =
      //If found in current scope, return true to go to catch
      if(scopeWithExceptionMap.contains(scopeHierarchyString)) {
        if (scopeWithExceptionMap(scopeHierarchyString).contains(exceptionName)) {
          val catchCommands = scopeWithExceptionMap(scopeHierarchyString)(exceptionName)
          catchCommands.foreach(command =>
            command.eval
          )
        }
        else {
          if (!scopeHierarchyString.contains(".")) {
            throw new RuntimeException("No catch block found.")
          }
        }
      }
      //If not found in current scope, recurse towards main.
      else {
        //We are in main scope
        if (!scopeHierarchyString.contains(".")) {
          if(scopeWithExceptionMap.contains(scopeHierarchyString)) {
            if (!scopeWithExceptionMap(scopeHierarchyString).contains(exceptionName))
              throw new RuntimeException("No catch block found.")
            else {
              val catchCommands = scopeWithExceptionMap(scopeHierarchyString)(exceptionName)
              catchCommands.foreach(command =>
                command.eval
              )
            }
          }
          else {
            throw new RuntimeException("No catch block found.")
          }
        }
        //Else main.scope1.scope2 turns to main.scope1
        val reducedScopeString = scopeHierarchyString.substring(0, scopeHierarchyString.lastIndexOf("."))
        recurseCheckForException(exceptionName, reducedScopeString)
      }

    //Helper method that creates the data structure in the readme. Used for indefinite nesting of classes.
    private def recurseAddClassToMap(className: String, fields: Array[Field], constructor: Constructor, methods: Array[Method], nestedC: Any, nestedI: Any): (String, mutable.Map[String, Any]) =
      val methodAccessMap = mutable.Map[String, Any]("private" -> mutable.Map[String, Any](),
        "public" -> mutable.Map[String, Any](), "protected" -> mutable.Map[String, Any](), "abstract" -> mutable.Map[String, Any]())
      for (inner <- 0 to methods.length - 2) {
        if (methods.length > 1) {
          for (outer <- inner + 1 until methods.length) {
            if (methods(inner)._1 == methods(outer)._1) {
              throw new RuntimeException("Can't have duplicate method names")
            }
          }
        }
      }
      for (method <- methods) {
        methodAccessMap(method._2.eval).asInstanceOf[mutable.Map[String, Any]] += (method._1 -> method._3)
      }
      val fieldAccessMap = mutable.Map[String, Any]("private" -> mutable.Map[String, Any](), "public" -> mutable.Map[String, Any](), "protected" -> mutable.Map[String, Any]())
      for (field <- fields) {
        fieldAccessMap(field._2.eval).asInstanceOf[mutable.Map[String, Any]] += (field._1 -> None)
        if (field._2.eval == "abstract")
          throw new RuntimeException("Can't have abstract fields.")
      }
      //If there is a nested class, add it under "nested" and evaluate it until no more nested.
      nestedC match {
        case classDef: ArithExp.ClassDef =>
          val stringMapTuple = recurseAddClassToMap(classDef.className, classDef.fields, classDef.constructor, classDef.methods, classDef.nestedC, classDef.nestedI)
          nestedI match {
            case interfaceDecl: ArithExp.InterfaceDecl =>
              val stringMapTuple2 = recurseAddInterfaceToMap(interfaceDecl.interfaceName, interfaceDecl.fields, interfaceDecl.methods, interfaceDecl.nestedC, interfaceDecl.nestedI)
              (className -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "constructor" -> constructor.eval,
                  "methods" -> methodAccessMap,
                  "name" -> className,
                  "parents" -> Array[String](),
                  "nestedC" -> (stringMapTuple._1 -> stringMapTuple._2),
                  "nestedI" -> (stringMapTuple2._1 -> stringMapTuple2._2)
                )
                )
            case _ =>
              (className -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "constructor" -> constructor.eval,
                  "methods" -> methodAccessMap,
                  "name" -> className,
                  "parents" -> Array[String](),
                  "nestedC" -> (stringMapTuple._1 -> stringMapTuple._2),
                  "nestedI" -> None
                )
                )
          }
        case _ =>
          nestedI match {
            case interfaceDecl: ArithExp.InterfaceDecl =>
              val stringMapTuple2 = recurseAddInterfaceToMap(interfaceDecl.interfaceName, interfaceDecl.fields, interfaceDecl.methods, interfaceDecl.nestedC, interfaceDecl.nestedI)
              (className -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "constructor" -> constructor.eval,
                  "methods" -> methodAccessMap,
                  "name" -> className,
                  "parents" -> Array[String](),
                  "nestedC" -> None,
                  "nestedI" -> (stringMapTuple2._1 -> stringMapTuple2._2)
                )
                )
            case _ =>
              (className -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "constructor" -> constructor.eval,
                  "methods" -> methodAccessMap,
                  "name" -> className,
                  "parents" -> Array[String](),
                  "nestedC" -> None,
                  "nestedI" -> None
                )
                )
          }
      }

    //"interface" -> Map("name" -> "interfaceName"
    //                   "fields" -> Map("private" -> Map("fieldName" -> Any)
    //                                   "public" -> Map("fieldName" -> Any)
    //                                   "protected" -> Map("fieldName" -> Any)
    //                   "methods" -> Map("protected" -> Map("methodName" -> Array[ArithExp])
    //                                    "private" -> Map("methodName" -> Array[ArithExp])
    //                                    "private" -> Map("methodName" -> Array[ArithExp])
    //                                    "abstract" -> Map("methodName" -> Array[ArithExp])
    //                   "innerC" -> Map("className" -> class)
    //                   "innerI" -> Map("interfaceName" -> interface)
    private def recurseAddInterfaceToMap(interfaceName: String, fields: Array[Field], methods: Array[Method], nestedC: Any, nestedI: Any): (String, mutable.Map[String, Any]) = {
      //Interface only has abstract methods.
      val methodAccessMap = mutable.Map[String, Any]("abstract" -> mutable.Map[String, Any]())
      for (inner <- 0 to methods.length - 2) {
        if (methods.length > 1) {
          for (outer <- inner + 1 until methods.length) {
            if (methods(inner)._1 == methods(outer)._1) {
              throw new RuntimeException("Can't have duplicate method names")
            }
          }
        }
      }
      for (method <- methods) {
        if (method._2.eval != "abstract")
          throw new RuntimeException("Methods declared in an interface must be abstract.")
        methodAccessMap(method._2.eval).asInstanceOf[mutable.Map[String, Any]] += (method._1 -> method._3)
      }
      val fieldAccessMap = mutable.Map[String, Any]("private" -> mutable.Map[String, Any](), "public" -> mutable.Map[String, Any](), "protected" -> mutable.Map[String, Any]())
      for (field <- fields) {
        fieldAccessMap(field._2.eval).asInstanceOf[mutable.Map[String, Any]] += (field._1 -> None)
        if (field._2.eval == "abstract")
          throw new RuntimeException("Can't have abstract fields.")
      }
      //If there is a nested class, add it under "nested" and evaluate it until no more nested.
      nestedC match {
        case classDef: ArithExp.ClassDef =>
          val stringMapTuple = recurseAddClassToMap(classDef.className, classDef.fields, classDef.constructor, classDef.methods, classDef.nestedC, classDef.nestedI)
          nestedI match {
            case interfaceDecl: ArithExp.InterfaceDecl =>
              val stringMapTuple2 = recurseAddInterfaceToMap(interfaceDecl.interfaceName, interfaceDecl.fields, interfaceDecl.methods, interfaceDecl.nestedC, interfaceDecl.nestedI)
              (interfaceName -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "methods" -> methodAccessMap,
                  "name" -> interfaceName,
                  "parents" -> Array[String](),
                  "nestedC" -> (stringMapTuple._1 -> stringMapTuple._2),
                  "nestedI" -> (stringMapTuple2._1 -> stringMapTuple2._2)
                )
                )
            case _ =>
              (interfaceName -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "methods" -> methodAccessMap,
                  "name" -> interfaceName,
                  "parents" -> Array[String](),
                  "nestedC" -> (stringMapTuple._1 -> stringMapTuple._2),
                  "nestedI" -> None
                )
                )
          }
        case _ =>
          nestedI match {
            case interfaceDecl: ArithExp.InterfaceDecl =>
              val stringMapTuple2 = recurseAddInterfaceToMap(interfaceDecl.interfaceName, interfaceDecl.fields, interfaceDecl.methods, interfaceDecl.nestedC, interfaceDecl.nestedI)
              (interfaceName -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "methods" -> methodAccessMap,
                  "name" -> interfaceName,
                  "parents" -> Array[String](),
                  "nestedC" -> None,
                  "nestedI" -> (stringMapTuple2._1 -> stringMapTuple2._2)
                )
                )
            case _ =>
              (interfaceName -> mutable.Map[String, Any]
                ("fields" -> fieldAccessMap,
                  "methods" -> methodAccessMap,
                  "name" -> interfaceName,
                  "parents" -> Array[String](),
                  "nestedC" -> None,
                  "nestedI" -> None
                )
                )
          }
      }
    }

    //Helper method that checks for method in current class
    private def checkCurrentClassForMethod(methodMap: mutable.Map[String, Array[ArithExp]], methodName: String): Boolean =
      for (method <- methodMap) {
        if (method._1 == methodName) {
          for (commands <- method._2) {
            commands.eval
          }
          return true
        }
      }
      false

    //Helper method that goes from child class to oldest ancestor
    private def recurseClassHierarchy(className: String, methodName: String): Boolean =
      val classBlueprintMap = classMap(className).asInstanceOf[mutable.Map[String, Any]]
      val classMethodMap = classBlueprintMap("methods").asInstanceOf[mutable.Map[String, Any]]
      val classPublicMethodMap = classMethodMap("public").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
      val classProtectedMethodMap = classMethodMap("protected").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
      val classImplementedAbstractMethodMap = classMethodMap("abstract").asInstanceOf[mutable.Map[String, Array[ArithExp]]]
      if (!checkCurrentClassForMethod(classPublicMethodMap, methodName)) {
        if (!checkCurrentClassForMethod(classProtectedMethodMap, methodName)) {
          if (!checkCurrentClassForMethod(classImplementedAbstractMethodMap, methodName)) {
            val classParentArray = classBlueprintMap("parents").asInstanceOf[Array[String]]
            if (!classParentArray.isEmpty) {
              recurseClassHierarchy(classParentArray(0), methodName)
            }
            //Current class has no parents
            else {
              throw new RuntimeException("Method not found")
            }
          }
        }
      }
      true




  @main def createSetTheoryInputSession(): Unit =
    import ArithExp.*