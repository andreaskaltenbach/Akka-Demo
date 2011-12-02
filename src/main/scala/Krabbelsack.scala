package se.stendahls.krabbelsack

import akka.actor.Actor.actorOf
import collection.mutable.{DoubleLinkedList, Queue}
import java.util.Random
import javax.print.attribute.standard.PresentationDirection
import akka.actor.{PoisonPill, ActorRef, Actor}

object Application extends App {

  val krabbelsack = new Krabbelsack

  val krabbelsackActor = actorOf[KrabbelsackActor]
  krabbelsackActor.start

  val erik = actorOf(new PersonActor("Erik", Beer, 1000, krabbelsackActor))
  val matsola = actorOf(new PersonActor("Mats-Ola", Honey, 1000, krabbelsackActor))
  val daniel = actorOf(new PersonActor("Daniel", Volleyball, 12000, krabbelsackActor))
  val anders = actorOf(new PersonActor("Anders", Chicken, 13000, krabbelsackActor))
  Set(erik, matsola, daniel, anders).foreach(_.start)

  val begin = new Start(krabbelsack)
  Set(erik, matsola, daniel, anders).foreach(_ ! begin)

  Thread.sleep(50000)
  println("Sending poison pill to everybody")
  Set(erik, matsola, daniel, anders, krabbelsackActor).foreach(_ ! PoisonPill)

}

// messages
case class Start(krabbelsack:Krabbelsack)

case class Present(name:String)

object Beer extends Present("Beer")
object Honey extends Present("Honey")
object Volleyball extends Present("Volleyball")
object Chicken extends Present("Chicken")

class PersonActor(name:String, initialPresent:Present, waitingTime:Long, krabbelsackActor:ActorRef) extends Actor {

  def receive = {
    case Start(krabbelsack) => {
      println(name + " is putting his present " + initialPresent + " into the krabbelsack.")
      println("------------------------------------")
      krabbelsackActor ! initialPresent
    }
    case Present(p) => {
      println(name + " got a " + p + " as present.")
      println("------------------------------------")
      Thread.sleep(waitingTime)
      println(name + " does not want his " + p + " and returns it to the krabbelsack.")
      println("------------------------------------")
      krabbelsackActor ! Present(p)
    }
  }
}

class Krabbelsack {
  val presents:Set[Present] = Set()
}


class KrabbelsackActor extends Actor {

  var personQueue:Queue[ActorRef] = Queue()
  var presents:Vector[ScalaObject] = Vector()

  def receive = {

    case Present(p) => {
      personQueue += self.sender.get
      presents = Present(p) +: presents

      while(presents.size > 1) {

        // fetch random present
        val randomIndex = new Random().nextInt(presents.size)
        val present = presents(randomIndex)
        presents = presents filter (_ != present)

        // give present to next person in queue
        val personActor = personQueue.dequeue
        personActor ! present
      }
    }
  }
}

case class EmployeeWish(previousPresent:Present)