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


    public static void sendOrderConfirmation(String firstName, String lastName, String email, int orderId) throws IOException {
        // Get the API key
        SendGrid sg = new SendGrid(API_KEY);


        Email from = new Email(companyMail);
        from.setName("Johannes Fog Byggemarked");

        Mail mail = new Mail();
        mail.setFrom(from);


        Personalization personalization = new Personalization();

        personalization.addTo(new Email(email));
        personalization.addDynamicTemplateData("name", firstName + " " + lastName);
        personalization.addDynamicTemplateData("orderId", "1234");
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
}
