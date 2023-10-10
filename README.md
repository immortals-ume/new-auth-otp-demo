# new-auth-otp-demo

## Steps to Setup the Spring Boot Back end app

1. **Clone the application**

   ```bash
   git clone "provide your github"
   ```

3. **Change MySQL username and password as per your MySQL installation**

    + open `src/main/resources/application.yaml` file.

    + change `spring.datasource.username` and `spring.datasource.password` properties as per your mysql installation

    + open `src/main/resources/application.yaml` file.

    + change `spring.mail.username` and `spring.mail.password` properties as per your smtp gmail configuration
      4.**Run the app**

   You can run the spring boot app by typing the following command -

   ```bash
   mvn spring-boot:run
   ```

   The server will start on port 8081.

5. Project Flow

User Registration, Login, and OTP Generation

User Registration

      User initiates the registration process.User provides necessary information (e.g., name, email, password).
      The system validates the user's data and stores it in the database.

User Login

      After registration, the user returns to the platform.
         User enters their registered email and password.
   
OTP Generation on Login
      
      Upon entering valid login credentials, the system generates a one-time OTP (One-Time Password).
      Sending OTP The generated OTP is sent to the user's registered email address for verification.
         The system uses an email service or API to deliver the OTP.
   
OTP Verification

      The user checks their email for the OTP.
      User enters the received OTP into the login page.

OTP Verification Process
   
      The system verifies the entered OTP against the generated OTP.
      If the OTP is valid, the user is granted access to their account. (Jwt Tocken isgenrated)