package zonnic.land.zonniccustomsuffix;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    ZonnicCustomSuffix plugin = ZonnicCustomSuffix.getPlugin();

    public boolean hasBannedCharacters(String string) {
        List<String> specialCharacters = plugin.getConfig().getStringList("suffix.banned-characters");
        List<Character> charactersInString = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) charactersInString.add(string.charAt(i));

        for (Character character : charactersInString) {
            String charString = String.valueOf(character);
            for (String stringAt : specialCharacters) {
                if (charString.equals(stringAt)) return false;
            }
        }
        return true;
    }

    public String removeColorTags(String string) {
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            String temp = String.valueOf(string.charAt(i));
            if (temp.equals("&")) {
                i++;
            } else {
                newString.append(temp);
            }
        }
        return newString.toString();
    }

}
