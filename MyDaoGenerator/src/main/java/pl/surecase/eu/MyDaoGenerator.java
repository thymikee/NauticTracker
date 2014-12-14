package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "nautictracker");

        /* Trip entity */
        Entity trip = schema.addEntity("Trip");
        trip.addIdProperty();
        trip.addLongProperty("remote_id");
        trip.addStringProperty("title");
        trip.addBooleanProperty("saved");

        /* TrackPoint entity */
        Entity trackPoint = schema.addEntity("TrackPoint");
        trackPoint.addLongProperty("trip_id").primaryKey();
        trackPoint.addStringProperty("latitude");
        trackPoint.addStringProperty("longitude");
        trackPoint.addDateProperty("timestamp");
        trackPoint.addLongProperty("sequence");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}