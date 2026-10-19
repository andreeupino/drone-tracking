import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Detection } from './detection.model';

const API = 'http://localhost:8080';
const WS = 'ws://localhost:8080/ws/live';

@Injectable({ providedIn: 'root' })
export class FlightService {
  private http = inject(HttpClient);

  getActiveFlights(): Observable<Record<string, Detection[]>> {
    return this.http.get<Record<string, Detection[]>>(`${API}/api/flights/active`);
  }

  live$(): Observable<Detection> {
    return new Observable<Detection>(sub => {
      let ws: WebSocket;
      let closed = false;
      const connect = () => {
        ws = new WebSocket(WS);
        ws.onmessage = e => sub.next(JSON.parse(e.data));
        ws.onclose = () => { if (!closed) setTimeout(connect, 2000); };
      };
      connect();
      return () => { closed = true; ws.close(); };
    });
  }
}