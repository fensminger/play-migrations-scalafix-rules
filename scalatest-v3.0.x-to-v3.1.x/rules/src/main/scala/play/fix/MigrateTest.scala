package play.fix

import play.fix.Classes._
import scalafix.v1._

import scala.meta._

class MigrateTest extends SemanticRule("MigrateTest") {

  override def fix(implicit doc: SemanticDocument): Patch = {
//    println("MigrateTest Tree.structure: " + doc.tree.structure)

    val imports = Imports(doc.tree)

    var isMustMatcher = false
    var isShouldMatcher = false

    val res = doc.tree
      .collect {
        case Importer(q"org.scalatest", importedTypes) =>
          importedTypes.collect {
            case i @ importee"FlatSpec" =>
              imports.ensureImport(importer"org.scalatest.flatspec.AnyFlatSpec")
              Patch.removeImportee(i)
            case i @ importee"MustMatchers" =>
              isMustMatcher = true
              imports.ensureImport(importer"org.scalatest.matchers.must.Matchers")
              Patch.removeImportee(i)
            case i @ importee"Matchers" =>
              isShouldMatcher = true
              imports.ensureImport(importer"org.scalatest.matchers.should.Matchers")
              Patch.removeImportee(i)
          }.asPatch

        case Importer(q"org.scalatest.prop", importedTypes) =>
          importedTypes.collect {
            case i @ importee"GeneratorDrivenPropertyChecks" =>
              imports.ensureImport(importer" org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks")
              Patch.removeImportee(i)
          }.asPatch

        case t @ Defn.Class(_, _, _, _, _) =>
          val fixed = t.mapInit(inits => {
            inits.map(init => {
              init.tpe.syntax match {
                case "FlatSpec" => init.copy(tpe = Type.Name("AnyFlatSpec"))
                case "MustMatchers" => init.copy(tpe = Type.Name("Matchers"))
                case "GeneratorDrivenPropertyChecks" => init.copy(tpe = Type.Name("ScalaCheckDrivenPropertyChecks"))
                case _ => init
              }
            })
          })
          ExtraPatch.replaceClassDef(t, fixed)

      }
      .asPatch
      .atomic +
      imports.asPatch

    if (isMustMatcher && isShouldMatcher) {
      // TODO Improve error management or rename Matcher
      println("ERROR : The generated code does not compile. 2 matchers with the same name")
    }

    res
  }

}
