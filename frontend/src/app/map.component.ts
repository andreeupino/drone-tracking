import { AfterViewInit, Component, OnDestroy, inject } from '@angular/core';
import { Subscription } from 'rxjs';
import * as L from 'leaflet';
import { FlightService } from './flight.service';
import { Detection } from './detection.model';

const STALE_MS = 120_000; 

interface FlightView {
  line: L.Polyline;
  marker: L.CircleMarker;
  lastSeen: number;
  ids: Set<string>; 
}

@Component({
  selector: 'app-map',
  standalone: true,
  template: '<div id="map"></div>',
  styles: ['#map { height: 100vh; width: 100%; }'],
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private svc = inject(FlightService);
  private map!: L.Map;
  private flights = new Map<string, FlightView>();
  private sub = new Subscription();
  private cleanupTimer?: ReturnType<typeof setInterval>;
  private loaded = false;

  ngAfterViewInit() {
    this.map = L.map('map').setView([37.18, -3.60], 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap',
    }).addTo(this.map);

    const buffer: Detection[] = [];
    this.sub.add(
      this.svc.live$().subscribe(d => (this.loaded ? this.add(d, true) : buffer.push(d)))
    );

    this.sub.add(
      this.svc.getActiveFlights().subscribe(res => {
        Object.values(res).flat().forEach(d => this.add(d, false));
        this.loaded = true;
        buffer.forEach(d => this.add(d, true));
      })
    );

    this.cleanupTimer = setInterval(() => this.removeStale(), 5000);
  }

  private add(d: Detection, live: boolean) {
    let f = this.flights.get(d.sn);
    if (f?.ids.has(d.id)) return;

    const p: L.LatLngTuple = [d.droneLocation.lat, d.droneLocation.lon];
    if (!f) {
      const tip = document.createElement('span');
      tip.textContent = `${d.name} (${d.sn})`;

      f = {
        line: L.polyline([], { weight: 3 }).addTo(this.map),
        marker: L.circleMarker(p, { radius: 7 }).addTo(this.map).bindTooltip(tip),
        lastSeen: 0,
        ids: new Set<string>(),
      };
      this.flights.set(d.sn, f);
    }

    f.ids.add(d.id);
    f.line.addLatLng(p);
    f.marker.setLatLng(p);
    f.lastSeen = live
      ? Date.now() 
      : Math.max(f.lastSeen, new Date(d.timestamp).getTime());
  }

  private removeStale() {
    const limit = Date.now() - STALE_MS;
    for (const [sn, f] of this.flights) {
      if (f.lastSeen < limit) {
        f.line.remove();
        f.marker.remove();
        this.flights.delete(sn); 
      }
    }
  }

  ngOnDestroy() {
    clearInterval(this.cleanupTimer);
    this.sub.unsubscribe();
    this.map?.remove();
  }
}