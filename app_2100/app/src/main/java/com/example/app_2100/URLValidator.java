import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {
    public static boolean isValidURL(String url) {
        String urlPattern = "^(https?|ftp)://(www\\.)?[a-zA-Z0-9]+(\\.[a-zA-Z]+)+([/?].*)?$";

        Pattern pattern = Pattern.compile(urlPattern);

        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }
}
