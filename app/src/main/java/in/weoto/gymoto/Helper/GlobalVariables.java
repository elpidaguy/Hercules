package in.weoto.gymoto.Helper;

import java.text.NumberFormat;
import java.util.Locale;

public class GlobalVariables {
    public static String url= "http://159.65.156.119:3000/api/";

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }

    public  static String toIndianCurr(Double money)
    {

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en","IN"));
        String moneyString = formatter.format(money);


        return  moneyString;
    }
}
