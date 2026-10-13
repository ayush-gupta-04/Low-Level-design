## Coffee Vending Machine LLD — Core Requirements ☕
1. Coffee Selection
- The machine should support multiple coffee types, such as Espresso, Latte, and Cappuccino.
- Each coffee should have a name, price, and recipe specifying the required ingredients and quantities.
- The user should be able to select a coffee from the available menu.

2. Ingredient Inventory
- The machine should maintain the available quantity of each ingredient, such as coffee powder, milk, water, and sugar.
- The machine should check whether sufficient ingredients are available before preparing a coffee.
- The machine should deduct the required ingredients after confirming availability.
- The machine should allow ingredients to be refilled.

3. Payment Management
- The user should be able to insert money in multiple installments.
- The machine should validate that the total payment covers the selected coffee's price.
- The machine should return the appropriate change when the payment exceeds the price.
- The machine should refund the inserted amount if the user cancels the transaction or the coffee cannot be prepared.

4. Coffee Preparation and Dispensing
- The machine should prepare coffee according to its recipe.
- The machine should support optional customizations, such as extra sugar or caramel syrup.
- The machine should dispense the coffee only after successful payment and ingredient deduction.

5. Transaction and Machine State
- The machine should support the states required for a transaction, such as ready, selecting coffee, paid.
- The user should be able to cancel a transaction before coffee preparation begins.
- The machine should reset to its initial state after a successful transaction or cancellation.
- The machine should prevent invalid operations, such as dispensing without payment or selecting another coffee during an active transaction.