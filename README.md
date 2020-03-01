# Basket pricer

A supermarket basket pricing tool, designed to handle a variety of pricing schemes and types of special offers.

Made using Java 8 and Maven 3.6 as a build tool in an Object-Oriented style.

The `com.github.neiljustice.basketpricer.demo.Demo` main class is a good place to start to get a feel for how the program works.

### Usage

Run `mvn clean test` to run the unit tests.

Run `mvn clean package` to build.

Once built, run `java -jar target/basket-pricer-1.0-SNAPSHOT.jar ` to run the demo basket pricing. This includes 2 sample baskets being priced.

(all commands run from the root directory of the project)

## Features

### Pricing schemes

Items can be priced per unit or by weight (in kilos).

### Types of offer

I wanted to represent special offers and pricing rules that apply beyond individual item purchases separately from items, so as not to have to modify an item in order to add or remove a special offer on that item.

The following types of offer / pricing rules have been implemented:

* Static discount: e.g. £3.00 off
* Buy X get Y: e.g. 'buy one get one free', '3 for 5', '2 for the price of 3'.
* Buy X for Y cost: e.g. '2 for £1.00', '3 for £3.00'
* Any in set for X cost: e.g. 'Any 2 chocolate bars for £2.00', 'Any 3 salads for £3.00'

More offer types could be added by creating more implementations of the `Offer` interface.

### Considerations

#### Item pricing

I wanted there to be a source of truth about what items are worth. Initially each instance of an item just declared its own price, so nothing was stopping 2 of the same products declaring that they have different prices. I didn't want individual offers to have to handle these sorts of problems. This is handled by the `ItemInfo` class. In a real-world scenario this would most likely be sourced from a database.also

#### Offer validation

As a result of the above, it becomes possible to validate for some strange offers, like £10 off a £2 product, or 'X for £Y' offers that increase the cost of the items.

Some input validation has been done for some classes, but more could definitely be added, such as in `BasketBuilder` (marked with TODOs)

#### Item IDs

Items don't have IDs, they have human-readable names which also function as IDs, which isn't great.  In a real-world setting I would want to change this.

Trying to add items to a basket that don't exist is a runtime error, rather than compile-time - but the alternative is hard-coding what products exist, which would require code changes whenever a new product was added.

#### Offer clashes

I haven't handled the case where 2 or more rules apply to the same item, but should not both be applied. Currently both would always be applied.

#### Big O / time complexity

Initially the time complexity was `O(offers * items)` - for each offer it was checking all items, as the basket stored items in a list, and items did not have a quantity field, so 3 cans of beans was represented by 3 'beans' entries in the list.  

Changing this was the main driver behind refactoring `StandardItem` and `WeightedItem` into one class, with `quantity` as a decimal field on the item. The `BasketBuilder` prevents the addition of decimal quantities of items supposed to only be purchasable in integer amounts.

Doing this meant I could represent a basket using a map from name to item, which meant that each offer did not need to check all items in the basket, but could access the items it applied to in `O(1)` time, making the total time complexity for pricing `O(offers)` - it will only scale with the number of offers.

The downside of this is that trying to add e.g. 3.265 cans of beans to a basket is a runtime error, rather than a compile-time error, which isn't great.

#### Currency and Internationalisation

I chose to represent currency values using the java `BigDecimal` class as that seemed to be the recommended approach, and meant not having to deal with floating point rounding errors.

A basic start has been made on making the program easily portable to other currencies.  All currency-specific information is encapsulated in the `CurrencyConfiguration` class, such as currency symbol, standard number of decimal places, and rounding mode. I chose the ROUND_HALF_EVEN rounding mode for the default as this also seemed to be the recommended choice for currency based on some quick research.

#### Testing

There are unit tests for each of the offer types implemented, and for the `Pricer` class, which is responsible for applying all offers to a basket and generating a pricing summary.  Ideally a few more of the classes would have had tests, such as the `BasketBuilder`.