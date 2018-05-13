package objects;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
public class Photo implements Serializable {
	private File photoFile;
	private String filePath;
	private String name;
	private String caption;
	private String albumName;
	private Calendar cal;
	private ArrayList<TagType> arrPhotoTags;

	/**
	 * Constructs a Photo object
	 */
	public Photo() {
		super();

		this.photoFile = null;
		this.filePath = "-";
		this.name = "-";
		this.caption = "-";
		this.albumName = "-";
		cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		arrPhotoTags = new ArrayList<TagType>(0);
	}

	/**
	 * Constructs a Photo object
	 * @param selectedFile A file containing the photo
	 * @param albumName the name of an album
	 * @param photoTags the tags the photo will have
	 */
	public Photo(File selectedFile, String albumName, ArrayList<TagType> photoTags) {
		super();

		this.photoFile = selectedFile;

		this.cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		this.cal.setTime(new Date((long) (selectedFile.lastModified())));

		this.filePath = selectedFile.getAbsolutePath();
		this.name = selectedFile.getName();
		this.caption = "-";
		this.albumName = albumName;

		this.arrPhotoTags = new ArrayList<TagType>();
		for (TagType t : photoTags) {
			this.arrPhotoTags.add(new TagType(t.getNameOfType(), t.getArrTags()));
		}
	}

	/**
	 * Constructs a Photo object
	 * @param filePath the path of the photo
	 * @param albumName the name of the album
	 * @param photoTags the tags the photo will have
	 */
	public Photo(String filePath, String albumName, ArrayList<TagType> photoTags) {
		File sourceImage = new File(filePath);
		this.photoFile = sourceImage;

		this.cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		this.cal.setTime(new Date((long) (sourceImage.lastModified())));

		this.filePath = sourceImage.getAbsolutePath();
		this.name = sourceImage.getName();
		this.caption = "-";
		this.albumName = albumName;

		this.arrPhotoTags = new ArrayList<TagType>();
		for (TagType t : photoTags) {
			this.arrPhotoTags.add(new TagType(t.getNameOfType(), t.getArrTags()));
		}
	}

	/**
	 * Returns the photo file
	 * @return photoFile the Photo File
	 */
	public File getPhotoFile() {
		return photoFile;
	}

	/**
	 * Returns the name of the photo
	 * @return name the name of the photo
	 */
	public String getName() {
		return name;
	}

	/**
	 * The date of the photo in simple form
	 * @return date the date of the photo in simple form
	 */
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYY");
		return sdf.format(cal.getTime());
	}

	/**
	 * Returns the path of the photo
	 * @return filePath the file path of the photo
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Returns the caption of the photo
	 * @return caption the caption of the photo
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption of the photo
	 * @param x the caption
	 */
	public void setCaption(String x) {
		this.caption = x;
	}

	/**
	 * Returns the name of the album of the photo
	 * @return albumName the album name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Transfers attributes from the parameters to this photo object
	 * @param selectedFile the file containing the photo
	 * @param currAlbumName the album name the photo belongs to
	 * @param caption 
	 * @param arrayList the tags of the photo
	 */
	public void setTransferAttributes(File selectedFile, String currAlbumName, String caption, ArrayList<TagType> arrayList) {

		this.photoFile = selectedFile;
		this.filePath = selectedFile.getAbsolutePath();
		this.name = selectedFile.getName();
		this.caption = caption;
		this.albumName = currAlbumName;
		this.cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		this.cal.setTime(new Date((long) (selectedFile.lastModified())));

		this.arrPhotoTags = new ArrayList<TagType>();
		for (TagType t : arrayList) {
			this.arrPhotoTags.add(new TagType(t.getNameOfType(), t.getArrTags()));
		}

	}

	/**
	 * Returns the date in raw form
	 * @return cal the date in Calendar form
	 */
	public Calendar getRawDate() {
		return cal;
	}

	/**
	 * Returns the tags of the photo
	 * @return arrPhotoTags the tags of the photo
	 */
	public ArrayList<TagType> getPhotoTags() {
		return arrPhotoTags;
	}

	/**
	 * Sets the tags of the photo
	 * @param newTags the tags the photo will contain.
	 */
	public void setPhotoTags(ArrayList<TagType> newTags) {
		this.arrPhotoTags = new ArrayList<TagType>();
		for (TagType t : newTags) {
			this.arrPhotoTags.add(new TagType(t.getNameOfType(), t.getArrTags()));
		}
	}

}
