package $package$

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

object Main {

  val logger = LoggerFactory.getLogger(getClass)
  
  def main(args: Array[String]): Unit = {

    val serviceName = "$name;format="word-space,upper-camel"$"
    
    if (args.nonEmpty) {
      // if an argument is passed, it's expected to be an alternative config file
      val configToLoad = args(0)
      logger.info(s"Actor system will be initialized with configuration file [\$configToLoad]")
      ActorSystem[Nothing](Main(), serviceName, ConfigFactory.load(configToLoad))
    } else {
      ActorSystem[Nothing](Main(), serviceName)
    }
  }

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing](context => new Main(context))
  }
}

class Main(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing](context) {
  val system = context.system

  AkkaManagement(system).start()
  ClusterBootstrap(system).start()

  override def onMessage(msg: Nothing): Behavior[Nothing] =
    this
}
