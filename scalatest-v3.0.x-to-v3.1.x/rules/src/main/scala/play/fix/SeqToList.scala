package play.fix

import scalafix.v1.{Patch, SemanticDocument, SemanticRule}
import play.fix.Classes._
import scalafix.v1._
import scala.meta._

class SeqToList extends SemanticRule("SeqToList") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    println("SeqToList Tree.structure: " + doc.tree.structure)
    val imports = Imports(doc.tree)

    /**
     * Change parameters type from
     *  - Seq to List
     *  - IndexedSeq to Vector
     *
     * @param v
     * @return
     */
    def replaceAllType(v: Type): Type = {
      v match {
        case Type.Name("Seq") => Type.Name("List")
        case Type.Name("IndexedSeq") => Type.Name("Vector")
        case Type.Apply(tt, stt) => Type.Apply(replaceAllType(tt), stt)
        case _ => v
      }
    }

    val res = doc.tree
      .collect {
        case v @ Type.Name(_) => Patch.replaceTree(v, replaceAllType(v).syntax).atomic
        case v @ Term.Name("toSeq") => Patch.replaceTree(v, Term.Name("toList").syntax).atomic
        case v @ Term.Name("toIndexedSeq") => Patch.replaceTree(v, Term.Name("toVector").syntax).atomic

//        case t @ Defn.Class(_, _, _, _, _) =>
//          val paramssFixed: scala.List[scala.List[scala.meta.Term.Param]] =
//            // Replace type for primary constructor
//            t.ctor.paramss.map(l => l.map(t => t.copy(t.mods, t.name, t.decltpe.map(v => replaceAllType(v)), t.default)))
//          val newT = t.copy(t.mods, t.name, t.tparams, t.ctor.copy(t.ctor.mods, t.ctor.name, paramssFixed), t.templ)
//          ExtraPatch.replaceClassDef(t, newT)
//
//        case o @ Defn.Object(_, _, _) =>
//          val newO = o.copy(o.mods, o.name, o.templ)
//          Patch.replaceTree(o, newO.syntax)

      }
      .asPatch
      .atomic +
      imports.asPatch

    res
  }
}
