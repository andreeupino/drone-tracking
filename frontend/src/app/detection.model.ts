export interface Location {
  lat: number; lon: number;
  aHeight?: number; distance?: number; fHeight?: number;
}
export interface Detection {
  id: string; sn: string; name: string; timestamp: string;
  droneLocation: Location; homeLocation?: Location; driverLocation?: Location;
}