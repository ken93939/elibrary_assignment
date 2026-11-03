## README

### Overview
This is an elibrary service implemented in java and sprigng boot similar to o reilly which allows the user to browse, consume digital content including books. Assume that there could be more digital content type in the future and the books/digital content are in pdf format stored in some object store.

### Architecture
Assume that it is a service created by a startup with limited resouces. 
I prefer to create the application as modular monolith in port and adapter architecture so that different domains are separate into different modules. 
First, it will be easier to split them into different services in the future due to lower coupling between the services.
Second, it gives clear boundary between different layers so that we can replace the infrastructure layer such as database choice, source of data more easily.

For this assignment, we try to avoid coupling between modules. However, due to the need of saving multiple records in one transaction, there is still some connection.
If different modules need to be separate in the future, transactions need to be reviewed. We will need to use other ways to coordinate the transactions using Outbox pattern, saga pattern.


### Domain modeling
I split the implementation into several modules, content, delivery, inventory, lending, security.
#### Content module
Content module stores digital-content metadata. Book contains book-specific details, and the book availability endpoint delegates to inventory.

#### Delivery module
Delivery module focuses on retrieving the digital content such as books from some storage. It contains content file which contains pointer to the content.

#### Inventory module
Inventory module tracks licence capacity and availability for digital content. Availability is calculated from capacity and active loans.

#### Lending module
Lending module handles book loan and return of the digital content. Loan represents the loan of digital content.

#### Security module
Security module handles the identification of the customer. Currently we use the basic authentication. We can delegate to IAM service in the future.

### API design
APIs are documented in docs/openapi.yaml. Currently it is not used for code generation. It can be enhanced to use the specification to generate the request and response models for spec-driven design.

### Future enhancement
1. Update to adopt IAM system. Validate incoming jwt against IAM server.
2. Split the service to multiple microservices if there is a need.



