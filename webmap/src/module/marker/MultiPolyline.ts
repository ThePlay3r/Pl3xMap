import * as L from "leaflet";
import {Util} from "../../util/Util";
import {Marker, Type} from "./Marker";

export class MultiPolyline extends Marker {

    // [[[0,0],[0,0],[0,0]],[[0,0],[0,0],[0,0]]]

    constructor(type: Type) {
        const data = type.data;
        const options = type.options;

        const lines = [];

        for (const points of data as unknown[][]) {
            const line = [];
            for (const point of points) {
                line.push(Util.toLatLng(point as L.PointTuple))
            }
            lines.push(line);
        }

        super(L.polyline(
            lines,
            {
                ...options?.properties,
                smoothFactor: 1.0,
                noClip: false,
                bubblingMouseEvents: true,
                interactive: true,
                attribution: undefined
            })
        );
    }
}
