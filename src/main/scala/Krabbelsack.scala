package se.stendahls.krabbelsack

import akka.actor.Actor.actorOf
import akka.actor.{ActorRef, Actor}

object Application extends App {

  val krabbelsack = new Krabbelsack

  val krabbelsackActor = actorOf[KrabbelsackActor]
  krabbelsackActor.start

  val erik = actorOf(new PersonActor("Erik", new Beer, krabbelsackActor))
  val matsola = actorOf(new PersonActor("Mats-Ola", new Honey, krabbelsackActor))
  val daniel = actorOf(new PersonActor("Daniel", new Volleyball, krabbelsackActor))
  val anders = actorOf(new PersonActor("Anders", new Chicken, krabbelsackActor))
  Set(erik, matsola, daniel, anders).foreach(_.start)

  val begin = new Begin(krabbelsack)
  Set(erik, matsola, daniel, anders).foreach(_ ! begin)





  //krabbelsackActor ! new SwitchPresent(new Beer)
  Set(krabbelsackActor, erik, matsola, daniel, anders).foreach(_.stop)
}

// switch message
case class Begin(krabbelsack:Krabbelsack)
case class SwitchPresent(present:Present)

trait Present
class Beer extends Present
class Honey extends Present
class Volleyball extends Present
class Chicken extends Present





class PersonActor(name:String, present:Present, krabbelsackActor:ActorRef) extends Actor {

  def receive = {
    case Begin(krabbelsack) => {
      println(name + " is putting his present " + present + " into the krabbelsack.")
      krabbelsackActor ! SwitchPresent(present)
    }
  }
}

class Krabbelsack {
  val presents:Set[Present] = Set()
}


class KrabbelsackActor extends Actor {

  val switchPresentMessages:Set[SwitchPresent] = Set()

  def receive = {

    case SwitchPresent(s) => {
      println("Switch request" + s)

      self.sender
      self reply "asf"
    }


  }
}