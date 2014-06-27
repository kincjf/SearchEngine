package common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
/**
 * Merge JSON object + { name : Number } 
 * @author KIMSEONHO
 *
 * @param <T1>			JSON Object
 */
public class MergeJson<T1> {
	//객체 + 숫자에 사용
	//다른 객체를 넣고 싶으면 잘 튜닝해서 쓰세요..
	private Gson gson1;
	private JsonElement jsonElement;
	
	/**
	 * Merging JSON Array TO JSON
	 * @param input1			JSON Array
	 * @param input2			integer
	 * @param propertyName		name which to be matching with input2	
	 */
	public void setMergeJson(T1 input1, Number input2, String propertyName) {
		this.gson1 = new Gson();
		this.jsonElement = this.gson1.toJsonTree(input1);
		this.jsonElement.getAsJsonObject().addProperty(propertyName, input2);
    }
	/**
	 * getter
	 * @return			merged jsonElement
	 */
    public JsonElement getMergeJson() {
        return this.jsonElement;
    }
    
    /**
     * gson data to json
     * @return		json data
     */
    public String printMergeJson() {
        String json = null;
        json = gson1.toJson(this.jsonElement);
    	
        return json;
    }
}
