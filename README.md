# Secret Santa Web App

This document outlines the end-to-end page flow for the Secret Santa web application.

## Page Flow

The application guides users through the process of setting up and participating in a Secret Santa gift exchange. The flow is as follows:

*   **Landing Page:**
    *   Introduction to the Secret Santa app.
    *   Option to start a new Secret Santa exchange or join an existing one.

*   **Start New Secret Santa:**
    *   User is prompted to enter details for the new exchange:
        *   Name of the exchange (e.g., "Office Holiday Party", "Family Christmas").
        *   Budget (optional).
        *   Date for gift exchange (optional).
    *   User enters their own name and email address to create the exchange.
    *   Option to add other participants manually by entering their names and email addresses.
    *   Option to generate a shareable link to invite participants.

*   **Join Existing Secret Santa:**
    *   User is prompted to enter the shareable link provided by the exchange organizer.
    *   User enters their name and email address to join the exchange.

*   **Exchange Page (after starting or joining):**
    *   Displays the name and details of the exchange.
    *   Shows the list of participants.
    *   Once all participants have joined, the organizer (or the system automatically after a set time) can trigger the drawing.
    *   Participants receive an email with the name of the person they need to buy a gift for.
    *   Options to view rules, budget, and exchange date.

*   **Drawing Process:**
    *   The application randomly matches participants, ensuring no one is matched with themselves and, ideally, avoiding direct swaps between two people (A gets B, B gets A).
    *   Emails are sent out to each participant revealing their assigned recipient.

*   **Post-Drawing:**
    *   Participants can refer back to the exchange page for details and rules.
    *   Communication features (optional): ability for participants to send anonymous messages to the organizer with questions.

## Getting Started (Developer Notes)

This is a minimal Java API service starter based on [Google Cloud Run Quickstart](https://cloud.google.com/run/docs/quickstarts/build-and-deploy/deploy-java-service). The server should run automatically when starting a workspace. To run manually, run:
```sh
mvn spring-boot:run
```