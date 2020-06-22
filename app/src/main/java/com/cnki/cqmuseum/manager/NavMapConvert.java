package com.cnki.cqmuseum.manager;

import com.ubtrobot.navigation.Marker;
import com.ubtrobot.navigation.NavMap;
import com.ubtrobot.navigation.Point;

import java.util.LinkedList;
import java.util.List;

class NavMapConvert {
    static final int DECIMAL = 100;

    private NavMapConvert() {
    }

    /**
     * Convert NavMap showing coordinates to NavMap of actual coordinates
     *
     * @param navMap The 'NavMap' Of Coordinates For Displaying
     * @return The 'NavMap' Of Coordinates For Actual Use
     */
    static NavMap convertToActual(NavMap navMap) {

        Point point = navMap.getGroundOverlayList().get(0).getOriginInImage();

        return new NavMap.Builder(navMap)
                .setMarkerList(displayToActual(navMap.getMarkerList(),
                        point.getX(),
                        point.getY(),
                        navMap.getScale()))
                .build();
    }

    /**
     * Convert NavMap of actual coordinates to NavMap showing coordinates
     *
     * @param navMap The 'NavMap' Of Coordinates For Actual Use
     * @return The 'NavMap' Of Coordinates For Displaying
     */
    static NavMap convertToDisplay(NavMap navMap) {

        Point point = navMap.getGroundOverlayList().get(0).getOriginInImage();

        return new NavMap.Builder(navMap)
                .setMarkerList(actualToDisplay(navMap.getMarkerList(),
                        point.getX(),
                        point.getY(),
                        navMap.getScale()))
                .build();
    }

    /**
     * Converts the MarkerList of the displayed coordinates to the MarkerList of the actual coordinates
     *
     * @param markers The 'MarkerList' Of Coordinates For Displaying
     * @param originX navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param originY navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale   navMap.getScale()
     * @return The 'MarkerList' Of Coordinates For Actual Use
     */
    private static List<Marker>
    displayToActual(List<Marker> markers, float originX, float originY, float scale) {
        LinkedList<Marker> markerList = new LinkedList<>();
        for (Marker marker : markers) {
            markerList.add(displayToActual(marker, originX, originY, scale));
        }

        return markerList;
    }

    /**
     * Convert the MarkerList of the actual coordinates to the MarkerList of the display coordinates
     *
     * @param markers The 'MarkerList' Of Coordinates For Actual Use
     * @param originX navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param originY navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale   navMap.getScale()
     * @return The 'MarkerList' Of Coordinates For Displaying
     */
    private static List<Marker>
    actualToDisplay(List<Marker> markers, float originX, float originY, float scale) {
        LinkedList<Marker> markerList = new LinkedList<>();
        for (Marker marker : markers) {
            markerList.add(actualToDisplay(marker, originX, originY, scale));
        }

        return markerList;
    }

    /**
     * Converts the 'Marker' of the displayed coordinates to the actual coordinates
     *
     * @param marker  The 'Marker' Of Coordinates For Displaying
     * @param originX navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param originY navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale   navMap.getScale()
     * @return The 'Marker' Of Coordinates For Actual Use
     */
    private static Marker displayToActual(Marker marker, float originX, float originY, float scale) {

        float actualX = displayX2MapX(marker.getPosition().getX(), originX, scale);
        float actualY = displayY2MapY(marker.getPosition().getY(), originY, scale);

        return new Marker.Builder(new Point(actualX, actualY))
                .setId(marker.getId())
                .setTagList(marker.getTagList())
                .setTitle(marker.getTitle())
                .setRotation(marker.getRotation())
                .setZ(marker.getZ())
                .setExtension(marker.getExtension())
                .setDescription(marker.getDescription())
                .build();
    }

    /**
     * Convert 'Marker' of actual coordinates to 'Marker' that displays coordinates
     *
     * @param marker  The 'Marker' Of Coordinates For Actual Use
     * @param originX navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param originY navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale   navMap.getScale()
     * @return The 'Marker' Of Coordinates For Displaying
     */
    private static Marker actualToDisplay(Marker marker, float originX, float originY, float scale) {

        float displayX = mapX2DisplayX(marker.getPosition().getX(), originX, scale);
        float displayY = mapY2DisplayY(marker.getPosition().getY(), originY, scale);

        return new Marker.Builder(new Point(displayX, displayY))
                .setId(marker.getId())
                .setTagList(marker.getTagList())
                .setTitle(marker.getTitle())
                .setRotation(marker.getRotation())
                .setZ(marker.getZ())
                .setExtension(marker.getExtension())
                .setDescription(marker.getDescription())
                .build();
    }

    /**
     * X axis display coordinates converted to actual coordinates
     *
     * @param displayX marker.getPosition().getX()
     * @param originX  navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param scale    navMap.getScale()
     * @return Actual Coordinates X
     */
    private static float displayX2MapX(float displayX, float originX, float scale) {
        return (displayX + originX) * scale;
    }

    /**
     * Y axis display coordinates converted to actual coordinates
     *
     * @param displayY marker.getPosition().getY()
     * @param originY  navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale    navMap.getScale()
     * @return Actual Coordinates Y
     */
    private static float displayY2MapY(float displayY, float originY, float scale) {
        return -(displayY - originY) * scale;
    }

    /**
     * X axis actual coordinate converted to display coordinates
     *
     * @param mapX    marker.getPosition().getX()
     * @param originX navMap.getGroundOverlayList().get(0).getOriginInImage().getX()
     * @param scale   navMap.getScale()
     * @return Display Coordinates X
     */
    private static float mapX2DisplayX(float mapX, float originX, float scale) {
        return (mapX / scale) - originX;
    }

    /**
     * Y axis actual coordinate converted to display coordinates
     *
     * @param mapY    marker.getPosition().getY()
     * @param originY navMap.getGroundOverlayList().get(0).getOriginInImage().getY()
     * @param scale   navMap.getScale()
     * @return Display Coordinates Y
     */
    private static float mapY2DisplayY(float mapY, float originY, float scale) {
        return originY - (mapY / scale);
    }

}
