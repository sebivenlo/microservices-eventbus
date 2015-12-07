---
title: A case study - Restaurant
---
Imagine for a second you are a restaurant owner and you want to improve your processes by applying micro services. Such restaurant could be managed in the following way:

![restaurant-setup](/img/restaurant-setup.png)

The **order service** receives orders and informs the **kitchen service** to cook certain meals. After a meal has been cooked the **order service** (a waiter) should get informed so the meal can get out to the actual customer. After a customer has finished his meal, the **payment service** takes over to receive the money from the customer. Also the payment service pays the employees such as the kitchen and the waiters.

### Creating the services

// todo
