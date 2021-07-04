import com.example.app._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    // what ever resources you need to start or initialize at the time of application
    // define it here in the init method of  scalatraBootstrap
    context.mount(new MainRequestHandler, "/*")
    context.mount(new TestRequestHandler, "/test/*")
  }
}
