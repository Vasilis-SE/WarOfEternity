package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.enums.HelpSectionEnum;

/**
 *
 * @author Vasilis Triantaris
 */
public class HelpFormInfoConfig {

    private String[] splitDataInSections(String content){
        return content.split("<break>");
    }

    public JSONArray getHelpInfoListContent(){
        JSONArray jsonHelpInfoArray = new JSONArray();
        String fileContent = TextFileProcessing.readResource("/helpInfo.txt");

        String[] fileSplittedInSection = this.splitDataInSections(fileContent);
        HelpSectionEnum[] sectionTitles = HelpSectionEnum.values();

        for(int i=0; i < fileSplittedInSection.length; i++){
            JSONObject jObj = new JSONObject();
            jObj.put("title", sectionTitles[i].getTitle());
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
