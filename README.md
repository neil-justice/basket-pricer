# Basket pricer

A supermarket basket pricing tool, designed to handle a variety of pricing schemes and types of special offers.

### Pricing schemes

Items can be priced per unit or by weight (in kilos).

### Types of offer

I wanted to represent offers and pricing rules that apply beyond individual item purchases separately from items, as it didn't seem correct to have to modify an item in order to add or remove a special offer on that item.

* Static discount: e.g. £3.00 off
* Buy X get Y: e.g. 'buy one get one free', '3 for 5', '2 for the price of 3'.

### Considerations

* I wanted there to be a source of truth about what items are worth.  Initially each instance of an item just declared its own price, so nothing was stopping 2 of the same products declaring that they have different prices.  I didn't want individual offers to have to handle these sorts of problems.
* As a result of the above, it becomes possible to validate for some strange offers, like £10 off a £2 can of beans.
* Items don't have IDs, they have human-readable names which also function as IDs, which isn't great.  In a real-world setting this would need changing.
* I haven't handled cases where 2 or more rules apply to the same item, but cannot be applied at once.
* Currently the time complexity is `O(offers * items)`, i.e. for each offer it checks all items.  If I have time there are some optimisations possible, such as:
    * sort the basket before applying offers, then offers can binary search for relevant items
    * 1 pass before applying offers to pick out all the item names and put them in a set.  Offers can then bail out early if they do not apply to anything in the set. 