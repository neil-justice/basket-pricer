# Basket pricer

A supermarket basket pricing tool, designed to handle a variety of pricing schemes and types of special offers.

### Pricing schemes

Items can be priced per unit or by weight (in kilos).

### Types of offer

I wanted to represent offers and pricing rules that apply beyond individual item purchases separately from items, as it didn't seem correct to have to modify an item in order to add or remove a special offer on that item.

* Static discount: e.g. £3.00 off
* Buy X get Y: e.g. 'buy one get one free', '3 for 5', '2 for the price of 3'.
* Buy X for Y cost: e.g. '2 for £1.00', '3 for £3.00'

### Considerations

* I wanted there to be a source of truth about what items are worth.  Initially each instance of an item just declared its own price, so nothing was stopping 2 of the same products declaring that they have different prices.  I didn't want individual offers to have to handle these sorts of problems.
* As a result of the above, it becomes possible to validate for some strange offers, like £10 off a £2 can of beans.
* Items don't have IDs, they have human-readable names which also function as IDs, which isn't great.  In a real-world setting this would need changing.
* I haven't handled cases where 2 or more rules apply to the same item, but cannot both be applied.
* Initially the time complexity was `O(offers * items)`, - for each offer it was checking all items, as the basket stored items in a list, and items did not have a quantity field, so 3 cans of beans was represented by beans being in the list 3 times.  When I refactored the `Item`s so that there was no class hierarchy, and `quantity` became a field on the item, this also made it easier to represent a basket using a map, which also meant that each offer did not need to check all items in the basket, but could access the items it applied to in `O(1)` time, making the total time complexity for pricing `O(offers)`.
* Trying to add items to a basket that don't exist is a runtime error, rather than compile-time - but the alternative is hard-coding what products exist, which would require code changes whenever a new product was added.
* Equally, trying to add e.g. 3.25 cans of beans is also a runtime error, rather than compile-time, which isn't great, but this tradeoff enabled the optimisation above which reduced the time complexity to `O(offers)`. 