package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Vasilis Triantaris
 */
public class HelpFormInfoConfig {

    private final String[] sectionTitles;

    public HelpFormInfoConfig(){
        this.sectionTitles = new String[]{
            "Commands", "Recognition", "NPC's",
            "Experience Mechanism", "Attribute Mechanism", "Game Map", "Music"
        };
    }

    private String[] splitDataInSections(String content){
        return content.split("<break>");
    }

    public JSONArray getHelpInfoListContent(){
        JSONArray jsonHelpInfoArray = new JSONArray();
        String fileContent = TextFileProcessing.readResource("/helpInfo.txt");

        String[] fileSplittedInSection = this.splitDataInSections(fileContent);

        for(int i=0; i < fileSplittedInSection.length; i++){
            JSONObject jObj = new JSONObject();
            jObj.put("title", this.sectionTitles[i].trim());
            jObj.put("content", fileSplittedInSection[i].trim());
            jsonHelpInfoArray.add(jObj);
        }

        return jsonHelpInfoArray;
    }

    /**
     * Method that finds the content of a help section by its title.
     *
     * @param sectionTitle The title of the section that the user selected.
     * @return Returns the content of the matching section, or an empty string if none matched.
     */
    public String getContentForSection(String sectionTitle){
        for(Object entry : getHelpInfoListContent()){
            JSONObject jObj = (JSONObject) entry;
            if(jObj.get("title").equals(sectionTitle))
                return (String) jObj.get("content");
        }

        return "";
    }
}
