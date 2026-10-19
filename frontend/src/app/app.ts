import { Component } from '@angular/core';
import { MapComponent } from './map.component';

@Component({
  selector: 'app-root',
  imports: [MapComponent],
  template: '<app-map />',
})
export class App {}