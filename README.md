# Currency Conversion System

## Description

This is a currency conversion system developed in Java using the Spring framework. The system allows you to register currencies and their exchange rates, as well as consume an external API to get up-to-date exchange rate information for currency conversions. The project uses Maven for dependency management, JUnit for testing, and integration with a frontend using Thymeleaf.

## Features

- Register currencies
- Register exchange rates
- Currency conversion using registered exchange rates
- Consume external API for exchange rate updates
- Web interface for interacting with the system

## Technologies Used

- **Java**
- **Spring Boot**
- **Maven**
- **JUnit**
- **Thymeleaf**

## Requirements

- JDK 11+
- Maven 3.6.0+
- An API key for a currency exchange service (e.g., [awesomeapi](https://docs.awesomeapi.com.br/api-de-moedas))

## Installation

1. Clone the repository:
   
   ```bash
   git clone https://github.com/your-username/your-repository.git
   cd your-repository

2. Configure the API key in the application.properties file:
   
    ```bash
    currency.api.url=https://api.exchangeratesapi.io/latest
    currency.api.key=YOUR_API_KEY

3. Build the project with Maven:
   
   ```bash
    mvn clean install

4. Run the application:

   ```bash
   mvn spring-boot:run
   
## Usage

After starting the application, go to http://localhost:8080 in your browser. You will see the interface to register currencies, exchange rates, and perform conversions.

## Tests

To run the tests, use Maven:
    
    mvn test

## Contributions

Contributions are welcome! Please open an issue or submit a pull request for improvements.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

If you have any questions or issues, feel free to open an issue or contact us.


