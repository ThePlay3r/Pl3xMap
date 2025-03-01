import * as L from "leaflet";
import {Marker, Type} from "./Marker";
import {Point} from "../util/Point";
import {getOrCreatePane, isset, toCenteredLatLng} from "../util/Util";

interface PolylineOptions extends L.PolylineOptions {
    key: string;
    points: Point[];
    pane: string;
}

export class Polyline extends Marker {
    constructor(type: Type) {
        const data = type.data as unknown as PolylineOptions;

        let options = {
            ...type.options?.properties,
            smoothFactor: 1.0,
            noClip: false,
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        };

        if (isset(data.pane)) {
            const dom = getOrCreatePane(data.pane);
            options = {
                ...options,
                pane: dom.className.split("-")[1]
            };
        }

        super(data.key, L.polyline(Polyline.createLine(data), options));
    }

    public update(raw: unknown[]): void {
        const data = raw as unknown as PolylineOptions;
        const polyline = this.marker as L.Polyline;
        polyline.setLatLngs(Polyline.createLine(data));
    }

    private static createLine(data: PolylineOptions): L.LatLng[] {
        const line = [];
        for (const point of data.points) {
            line.push(toCenteredLatLng(point))
        }
        return line;
    }
}
