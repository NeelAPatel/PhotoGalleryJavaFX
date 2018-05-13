package objects;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class User implements Serializable {

	private int id;
	private String username;
	private String password;

	// private static int totalAlbums; //CLASS VARIABLE
	private ArrayList<Album> arrAlbums;

	private ArrayList<TagType> arrTagTypes;
	public boolean logout = false;

	/**
	 * Constructs a User object
	 * 
	 * @param id
	 *            the id of the user
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public User(int id, String username, String password) {

		this.id = id;
		this.username = username;
		this.password = password;
		arrAlbums = new ArrayList<Album>();
		this.arrTagTypes = new ArrayList<TagType>();

		this.arrTagTypes.add(new TagType("Person"));
		this.arrTagTypes.add(new TagType("Location"));
	}

	/**
	 * Returns the id of the user
	 * 
	 * @return id the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets teh id of the user
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the username of the User
	 * 
	 * @return username the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the user
	 * 
	 * @param username
	 *            the username to be set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password of the user
	 * 
	 * @return password the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the passsword of the user
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the list of albums the user owns
	 * 
	 * @return arrAlbums the array of albums
	 */
	public ArrayList<Album> getAllAlbums() {
		return arrAlbums;
	}

	/**
	 * Adds a new album
	 * 
	 * @param album
	 *            new album
	 */
	public void addNewAlbum(Album album) {
		arrAlbums.add(album);
	}

	/**
	 * Gets a specific album
	 * 
	 * @param index
	 *            the index of the album
	 * @return album the specified album
	 */
	public Album getAlbum(int index) {
		return arrAlbums.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return username + ";" + password + ";id:" + id;
	}

	// TAGS

	/**
	 * Returns the list of tags the user will use across their albums
	 * 
	 * @return arrTagTypes the list of tags
	 */
	public ArrayList<TagType> getAllTagTypes() {
		return arrTagTypes;
	}

	/**
	 * Adds a new TagType to the collection
	 * 
	 * @param newType
	 *            a new Tag category
	 */
	public void addNewTagType(TagType newType) {
		this.arrTagTypes.add(newType);
	}

	/**
	 * Removes a tag category from the user
	 * 
	 * @param index
	 *            the index of the tagtype to be removed
	 */
	public void removeNewTagType(int index) {
		this.arrTagTypes.remove(index);
	}

	/*
	 * public void setTagTypes(ArrayList<TagType> list) { arrTagTypes = new
	 * ArrayList<TagType>(0); for(TagType t: list) { this.arrTagTypes.add(new
	 * TagType(t.getNameOfType(), t.getArrTags())); }
	 * //this.arrTagTypes.addAll(list); }
	 */

	/**
	 * Gets the array of types the user has
	 * 
	 * @return arrTagTypes the array of tagtypes
	 */
	public ArrayList<TagType> getTagTemplate() {
		return arrTagTypes;
	}

	/**
	 * Updates the tags iafter an add
	 */
	public void updateAddTags() {

		/*
		 * if tag exists in user then if tag exists in photo do nothing else if tag does
		 * not exist in photo add it else if tag does not exist in user then if tag
		 * exists in photo remove it else if tag does not exist in photo do nothing
		 */

		for (TagType myType : arrTagTypes) {
			for (Album a : arrAlbums) {
				for (Photo p : a.getAllPhotos()) {
					ArrayList<TagType> arrPhotoTag = p.getPhotoTags();

					boolean yesContains = false;
					// int indexOfContains = -1;
					for (TagType t : arrPhotoTag) {
						if (myType.getNameOfType().equals(t.getNameOfType())) {
							yesContains = true;
							// indexOfContains = arrPhotoTag.indexOf(t);
							break;
						}
					}
					if (yesContains) {
					} else {
						TagType newTag = new TagType(myType.getNameOfType());
						arrPhotoTag.add(newTag);
					}

				}
			}
		}

	}

	/**
	 * updates the tags after a delete
	 * 
	 * @param tagToDelete
	 *            the tag to delete
	 */
	public void updateDeleteTags(TagType tagToDelete) {

		boolean redo = false;
		for (Album a : arrAlbums) {
			for (Photo p : a.getAllPhotos()) {
				ArrayList<TagType> photoTags = p.getPhotoTags();
				int indexOfContains = -1;
				for (TagType pt : photoTags) {
					if (tagToDelete.getNameOfType().equals(pt.getNameOfType())) {
						indexOfContains = photoTags.indexOf(pt);
						redo = true;
					}
				}

				if (indexOfContains > -1) {
					photoTags.remove(indexOfContains);
				}

			}

		}

		if (redo) {
			updateDeleteTags(tagToDelete);
		}
	}

}
