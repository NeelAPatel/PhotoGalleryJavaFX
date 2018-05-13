package objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("serial")
public class Album implements Serializable {

	private String albumName;
	private Date startDate;
	private Date endDate;
	private String albumPath;
	private ArrayList<Photo> arrPhotos;
	private int numOfPics;

	/**
	 * Constructs an album object
	 * @param albumName name of the album
	 */
	public Album(String albumName) {
		this.albumName = albumName;
		// this.id = id;
		arrPhotos = new ArrayList<Photo>();
	}

	/**
	 * Constructs an album object
	 * @param albumName name of the album
	 * @param startDate start date
	 * @param endDate end date
	 * @param numOfPics number of pictures in the folder
	 */
	public Album(String albumName, Date startDate, Date endDate, int numOfPics) {
		this.albumName = albumName;

		this.startDate = startDate;
		this.endDate = endDate;
		arrPhotos = new ArrayList<Photo>();
	}

	/**
	 * Constructs an album object
	 * @param albumName name of the album
	 * @param allPhotos array of all Photos inside the album
	 */
	public Album(String albumName, ArrayList<Photo> allPhotos) {
		this.albumName = albumName;
		this.arrPhotos = new ArrayList<Photo>();
		for (Photo p : allPhotos) {
			this.arrPhotos.add(p);
		}
	}

	/**
	 * returns an array of Photos
	 * @return arrPhotos an array of photos
	 */
	public ArrayList<Photo> getAllPhotos() {
		return arrPhotos;
	}

	/**
	 * adds a photo to the album
	 * @param photo A photo
	 */
	public void addToAlbum(Photo photo) {
		arrPhotos.add(photo);
	}

	/**
	 * Gets the name of the album
	 * @return albumName the name of the album
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Gets the start date of the album
	 * @return startDate the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Gets the end dateof the album
	 * @return endDate the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Gets the number of Photos in the album
	 * @return numOfPics
	 */
	public int getNumOfPics() {
		return numOfPics;
	}

	/**
	 * Gets the path of the album
	 * @return albumPath the path
	 */
	public String getAlbumPath() {
		return albumPath;
	}

	/**
	 * Sets the name of the album
	 * @param albumName the name of the album
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	/**
	 * Sets the start date
	 * @param startDate the start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the end date
	 * @param endDate the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * sets the number of pictures in the album
	 * @param numOfPics the number of pics
	 */
	public void setNumOfPics(int numOfPics) {
		this.numOfPics = numOfPics;
	}

	/**
	 * Sets the path of the album
	 * @param albumPath the path
	 */
	public void setAlbumPath(String albumPath) {
		this.albumPath = albumPath;
	}

	/**
	 * Returns a photo from the array
	 * @param indexOf the index of the photo
	 * @return photo the photo
	 */
	public Photo getPhoto(int indexOf) {
		return arrPhotos.get(indexOf);
	}

	/**
	 * Gets the date of the oldest photo
	 * @return date the oldest date
	 */
	public String getEarliestDate() {
		Calendar min = Calendar.getInstance();
		for (Photo p : arrPhotos) {
			if (p.getRawDate().before(min)) {
				min = p.getRawDate();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYY");
		return sdf.format(min.getTime());
	}

	/**
	 * Gets the date of the newest photo
	 * @return date the newest date
	 */
	public String getLatestDate() {
		Calendar max = Calendar.getInstance();

		max.setTime(new Date(Long.MIN_VALUE));

		for (Photo p : arrPhotos) {
			if (p.getRawDate().after(max)) {
				max = p.getRawDate();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYY");
		return sdf.format(max.getTime());
	}

}
