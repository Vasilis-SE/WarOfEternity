package GameFileConfiguration;

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

    private String[] SplitDataInSections(StringBuffer strBuff){
        return strBuff.toString().split("<break>");
    }

    public JSONArray GetHelpInfoListContent(){
        JSONArray jsonHelpInfoArray = new JSONArray();
        StringBuffer fileContent = TextFileProcessing.ReadResource("/helpInfo.txt");

        String[] fileSplittedInSection = this.SplitDataInSections(fileContent);

        for(int i=0; i < fileSplittedInSection.length; i++){
            JSONObject jObj = new JSONObject();
            jObj.put("title", this.sectionTitles[i].trim());
            jObj.put("content", fileSplittedInSection[i].trim());
            jsonHelpInfoArray.add(jObj);
        }

        return jsonHelpInfoArray;
    }
}
