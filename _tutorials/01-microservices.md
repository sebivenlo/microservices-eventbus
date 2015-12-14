---
title: What are Microservices?
---

# What we got until now
Most applications are developed as a monolithic application, especially bigger enterprise systems. This has several reasons, most IDEs are build to handle monolithic applications. Also this is easy to deploy since you only have one application. They are easy to test using front end tests through the UI.

![alt text](http://bits.citrusbyte.com/images/posts/2015-08-24-microservices-monolith.png "Monolithc Application")
[Imagesource](http://bits.citrusbyte.com/images/posts/2015-08-24-microservices-monolith.png)

But this approach imposes some problems:

- Harder for new developers to understand
- If one part of the application fails, it could take the whole application down
- This type of application doesn't scale well in the future
- Fixing bugs or developing new features get increasingly complex and time consuming
- Start up of the application can slow down development time
- Cant be used with Continues Integration systems because the whole application need to be redeployed
- Using new technologies get really time consuming and therefore expensive. You're stuck with what you started with


# What can we do against it
A new popular approach is the microservices pattern. Basically you devide your applications into several small interconnected services that all handle one part of your application. This style of developing Platforms is used by some of the biggest companies on the internet like Netflix, Amazon or Ebay. Every microservices exposes an API that is consumed by the other services if they need information from this service or want to interact with it.

![alt text](http://bits.citrusbyte.com/images/posts/2015-08-24-microservices-architecture.png "Microservice Application")
[Imagesource](http://bits.citrusbyte.com/images/posts/2015-08-24-microservices-architecture.png)

The micro service pattern significantly impacts the way data is stored inside of the application. Instead of one big database that contains all data, each service has its own database, they can also have different database schema or database types. This therefore often results in some data duplication, but its essential to ensure loose coupling.

Microservices have a couple of really cool advantages:

- The application is more modular, you can easily switch out technologies inside of a service.
- It's easier to understand for individual developers, since each service has its well defined responsibilities and boundaries
- Each service can be developed by an independent team
- Each service can be redeployed easily and often
- Is perfectly fine for continues integration and continues delivery in combination with infrastructure automation tools
- Easily scalable


Since there are no silver bullets there are also drawbacks:

- Increases complexity
- Testing is more complex
- Changes across multiple services are complex and need to be planned carefully
- Deployment is more complex, since you need to load balance the services or create an gateway through which the services are accessed
- Service discovery needs to solved, so each service knows where to reach the others

# Next up: case study

Go to the next chapter <a href="{{ "/tutorials/02-casestudy" | prepend: site.baseurl }}">here</a>!
