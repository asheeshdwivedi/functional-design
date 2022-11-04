package net.degoes

import java.time.Instant
import java.time.YearMonth
import scala.concurrent.duration.Duration

/*
 * INTRODUCTION
 *
 * Functional Design depends heavily on functional data modeling. Functional
 * data modeling is the task of creating precise, type-safe models of a given
 * domain using algebraic data types and generalized algebraic data types.
 *
 * In this section, you'll review basic functional domain modeling.
 */

/**
 * E-COMMERCE - EXERCISE SET 1
 *
 * Consider an e-commerce application that allows users to purchase products.
 */
object credit_card {

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a credit card, which must have:
   *
   *  * Number
   *  * Name
   *  * Expiration date
   *  * Security code
   */
  final case class CreditCard(number: String, name: String, exporationDate: YearMonth, securityCode: String)

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a product, which could be a physical product, such as a gallon of milk,
   * or a digital product, such as a book or movie, or access to an event, such
   * as a music concert or film showing.
   */
  sealed trait Product
  object Product {
    final case class Physical(dimensions: (Double, Double, Double), weight: Double) extends Product
    final case class Digital(url: String)                                           extends Product
    final case class Ticket(date: Instant)                                          extends Product
  }

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a product price, which could be one-time purchase fee, or a recurring
   * fee on some regular interval.
   */
  sealed trait PricingScheme { self =>
    def forever: PricingScheme  = PricingScheme.Recurring(self)
    def &&(that: PricingScheme) = PricingScheme.Both(self, that)
    def delay(time: Duration)   = PricingScheme.Delayed(self, time)
  }

  object PricingScheme {
    final case class OneTime(fee: Double) extends PricingScheme
    /*
     *final case class Recurring(
     *  initiationFee: Double,
     *  recurringFee: Double,
     *  interval: java.time.Duration,
     *  start: java.time.Instant
     *) extends PricingScheme
     */
    final case class Recurring(schema: PricingScheme)                   extends PricingScheme
    final case class Both(left: PricingScheme, right: PricingScheme)    extends PricingScheme
    final case class Delayed(schema: PricingScheme, duration: Duration) extends PricingScheme

    def apply(fee: Double): PricingScheme = OneTime(fee)
  }

}

/**
 * EVENT PROCESSING - EXERCISE SET 3
 *
 * Consider an event processing application, which processes events from both
 * devices, as well as users.
 */
object events {

  /**
   * EXERCISE
   *
   * Refactor the object-oriented data model in this section to a more
   * functional one, which uses only sealed traits and case classes.
   */
  final case class Event(id: Int, time: Instant, eventType: EventType)

  sealed trait EventType
  object EventType {
    final case class UserEvent(userName: String, userEventType: UserEventType)
    final case class DeviceEvent(deviceId: Int, deviceEventType: DeviceEventType)
  }

  sealed trait UserEventType
  object UserEventType {
    final case class Purchase(item: String, price: Double) extends UserEventType
    case object AccounCreated                              extends UserEventType
  }

  sealed trait DeviceEventType
  object DeviceEventType {
    final class SensorUpdated(reading: Option[Double]) extends DeviceEventType
    case object DeviceActivated                        extends DeviceEventType
  }

}

/**
 * DOCUMENT EDITING - EXERCISE SET 4
 *
 * Consider a web application that allows users to edit and store documents
 * of some type (which is not relevant for these exercises).
 */
object documents {
  final case class UserId(identifier: String)
  final case class DocId(identifier: String)
  final case class DocContent(body: String)

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, create a simplified but somewhat
   * realistic model of a Document.
   */
  final case class Document(userId: UserId, docId: DocId, content: DocContent)

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, create a model of the access
   * type that a given user might have with respect to a document. For example,
   * some users might have read-only permission on a document.
   */
  sealed trait AccessType {
    def canRead: Boolean  = ordinal >= 1
    def canWrite: Boolean = ordinal >= 2
    def canShare: Boolean = ordinal >= 3
    def ordinal: Int
  }
  object AccessType {
    sealed abstract class AbstractAccessType(val ordinal: Int) extends AccessType
    case object Denied                                         extends AbstractAccessType(0)
    case object ReadOnly                                       extends AbstractAccessType(1)
    case object ReadWrite                                      extends AbstractAccessType(0)
    case object ReadWriteShare                                 extends AbstractAccessType(3)
  }

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, create a model of the
   * permissions that a user has on a set of documents they have access to.
   * Do not store the document contents themselves in this model.
   */
  final case class DocPermissions(lookup: Map[DocId, AccessType])
}

/**
 * BANKING - EXERCISE SET 5
 *
 * Consider a banking application that allows users to hold and transfer money.
 */
object bank {

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, develop a model of a customer at a bank.
   */
  final case class Customer(
    userId: String,
    email: String,
    name: String,
    address: String,
    salt: Long,
    passwordHash: String
  )

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, develop a model of an account
   * type. For example, one account type allows the user to write checks
   * against a given currency. Another account type allows the user to earn
   * interest at a given rate for the holdings in a given currency.
   */
  sealed trait AccountType
  object AccountType {
    case object Checking                          extends AccountType
    final case class Saving(interestRate: Double) extends AccountType
  }

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, develop a model of a bank
   * account, including details on the type of bank account, holdings, customer
   * who owns the bank account, and customers who have access to the bank account.
   */
  final case class Account(currency: String, accountType: AccountType, holdings: Map[String, String], ownerId: String)
}

/**
 * STOCK PORTFOLIO - GRADUATION PROJECT
 *
 * Consider a web application that allows users to manage their portfolio of investments.
 */
object portfolio {

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, develop a model of a stock
   * exchange. Ensure there exist values for NASDAQ and NYSE.
   */
  type Exchange

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, develop a model of a currency
   * type.
   */
  type CurrencyType

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, develop a model of a stock
   * symbol. Ensure there exists a value for Apple's stock (APPL).
   */
  type StockSymbol

  /**
   * EXERCISE 4
   *
   * Using only sealed traits and case classes, develop a model of a portfolio
   * held by a user of the web application.
   */
  type Portfolio

  /**
   * EXERCISE 5
   *
   * Using only sealed traits and case classes, develop a model of a user of
   * the web application.
   */
  type User

  /**
   * EXERCISE 6
   *
   * Using only sealed traits and case classes, develop a model of a trade type.
   * Example trade types might include Buy and Sell.
   */
  type TradeType

  /**
   * EXERCISE 7
   *
   * Using only sealed traits and case classes, develop a model of a trade,
   * which involves a particular trade type of a specific stock symbol at
   * specific prices.
   */
  type Trade
}
