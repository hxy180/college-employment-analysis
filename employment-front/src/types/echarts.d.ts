declare module 'echarts' {
  interface ECharts {
    registerMap: (mapName: string, geoJson: unknown) => void;
  }
}

export {};
