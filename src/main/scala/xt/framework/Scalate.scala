package xt.framework

import java.io.{File, StringWriter, PrintWriter}

import org.fusesource.scalate.{TemplateEngine, Binding, DefaultRenderContext}
import org.fusesource.scalate.scaml.ScamlOptions

object Scalate {
  val engine = new TemplateEngine
  engine.workingDirectory = new File("/tmp/scalate")
  engine.bindings = List(Binding("helper", "xt.framework.Helper", true))

  if (true) {  // production
    engine.allowReload  = false
    engine.allowCaching = true

    // Reduce the generated document size
    ScamlOptions.indent = ""
    ScamlOptions.nl     = ""

    // Significantly reduce the CPU overhead
    ScamlOptions.ugly = true
  } else {
    engine.allowReload  = true
    engine.allowCaching = false
  }

  def render(csasOrAs: String, helper: Helper): String = {
    val path     = csasOrAsToPath(csasOrAs, helper)
    val template = engine.load(path)

    val buffer  = new StringWriter
    val context = new DefaultRenderContext(engine, new PrintWriter(buffer))
    context.attributes("helper") = helper

    template.render(context)
    buffer.toString
  }

  //----------------------------------------------------------------------------

  private def csasOrAsToPath(csasOrAs: String, helper: Helper): String = {
    val csas1 = csasOrAsToCsas(csasOrAs, helper)

    val caa = csas1.split("#")
    val csas2 = caa(0).toLowerCase + "/" + caa(1)

    // FIXME: use viewPaths
    "colinh/view" + "/" + csas2 + ".scaml"
  }

  private def csasOrAsToCsas(csasOrAs: String, helper: Helper): String = {
    if (csasOrAs.indexOf("#") == -1)
      helper.param("controller").get + "#" + csasOrAs
    else
      csasOrAs
  }
}
