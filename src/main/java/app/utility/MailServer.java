package app.utility;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import java.io.IOException;

public class MailServer {
    private static final String API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final String companyMail = "albertevallentin@gmail.com";


    public static void sendOrderConfirmation(String firstName, String lastName, String email, int orderId, int cpWidth, int cpLength, String cpRoof, int shWidth, int shLength) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("orderId", orderId);
        personalization.addDynamicTemplateData("cpWidth", cpWidth);
        personalization.addDynamicTemplateData("cpLength", cpLength);
        personalization.addDynamicTemplateData("cpRoof", cpRoof);
        personalization.addDynamicTemplateData("shWidth", shWidth);
        personalization.addDynamicTemplateData("shLength", shLength);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-0f98ef4b7bcc48f7ab2ada3cb0e94f51";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }

    public static void sendOffer(String firstName, String lastName, String email, double shippingRate, double price, int orderId) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("shippingRate", shippingRate);
        personalization.addDynamicTemplateData("price", price);
        personalization.addDynamicTemplateData("orderId", orderId);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-57a60bdb987448de91345d3243786774";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }

    public static void sendNewOffer(String firstName, String lastName, String email, double shippingRate, double price, int orderId) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("shippingRate", shippingRate);
        personalization.addDynamicTemplateData("price", price);
        personalization.addDynamicTemplateData("orderId", orderId);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-281b6cd721b4485b8e6f8cb6ca4338f3";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }

    public static void denyNewOffer(String firstName, String lastName, String email, double shippingRate, double price, int orderId) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("shippingRate", shippingRate);
        personalization.addDynamicTemplateData("price", price);
        personalization.addDynamicTemplateData("orderId", orderId);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-30f2a8f4341a49ea910966629cd87694";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }




    public static void paymentConfirmed(String firstName, String lastName, String email, int orderId, double price) throws IOException {// Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("orderId", orderId);
        personalization.addDynamicTemplateData("price", price);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-dfe3fdeb2aef423dbf3f2297232cb3a1";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }

    public static void sendModifiedOrder(String firstName, String lastName, String email, int cpWidth, int cpLength, String cpRoof, int shWidth, int shLength, int orderId) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);

        // Create the email
        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("cpWidth", cpWidth);
        personalization.addDynamicTemplateData("cpLength", cpLength);
        personalization.addDynamicTemplateData("cpRoof", cpRoof);
        personalization.addDynamicTemplateData("shWidth", shWidth);
        personalization.addDynamicTemplateData("shLength", shLength);
        personalization.addDynamicTemplateData("orderId", orderId);
        mail.addPersonalization(personalization);

        mail.addCategory("carportapp");

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            // indsæt dit skabelonid herunder
            mail.templateId = "d-faeaf36c17a44aa28b1b1aa787900887";
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (
                IOException ex) {
            System.out.println("Error sending mail");
            throw ex;
        }
    }
}
