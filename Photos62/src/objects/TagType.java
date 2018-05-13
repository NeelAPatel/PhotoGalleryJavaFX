package objects;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TagType implements Serializable {

	private String nameOfType;
	private ArrayList<String> arrTags;

	/**
	 * Constructs a tagtype
	 */
	public TagType() {
		this.nameOfType = "newTag";
		this.arrTags = new ArrayList<String>();
	}

	/**
	 * Constructs a tagtype
	 * @param categoryName the name of the tag category
	 */
	public TagType(String categoryName) {
		this.nameOfType = categoryName;
		this.arrTags = new ArrayList<String>();
	}

	/**
	 * Constructs a tagtype
	 * @param categoryName the name of the tag category
	 * @param listOfTags the list of tags this category will contain
	 */
	public TagType(String categoryName, ArrayList<String> listOfTags) {
		this.nameOfType = categoryName;

		arrTags = new ArrayList<String>();
		for (String s : listOfTags) {
			this.arrTags.add(s);
		}
	}

	/**
	 * Returns the name of the type of tag
	 * @return nameOfType the name of the tag
	 */
	public String getNameOfType() {
		return nameOfType;
	}

	/**
	 * Sets the name of the category
	 * @param nameOfType name of category
	 */
	public void setNameOfType(String nameOfType) {
		this.nameOfType = nameOfType;
	}

	/**
	 * Returns the list of tags the object contains
	 * @return arrTags the list of tags
	 */
	public ArrayList<String> getArrTags() {
		return arrTags;
	}

	/**
	 * Sets the tags this object will contain
	 * @param newTags the list of tags
	 */
	public void setArrTags(ArrayList<String> newTags) {
		this.arrTags = new ArrayList<String>();
		for (String s : newTags) {
			this.arrTags.add(s);
		}
	}

	/**
	 * Adds a new tag to the object
	 * @param tag a new tag
	 */
	public void addTag(String tag) {
		this.arrTags.add(tag);
	}

}
