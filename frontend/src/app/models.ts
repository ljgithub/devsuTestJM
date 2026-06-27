// models.ts

export interface Cliente {
  id?: number;
  nombre: string;
  genero: string;
  edad: number;
  identificacion: string;
  direccion: string;
  telefono: string;
  clienteid: string;
  contrasena: string;
  estado: boolean;
}

export interface Cuenta {
  numeroCuenta: string;
  tipoCuenta: string; // 'Ahorros' or 'Corriente'
  saldoInicial: number;
  estado: boolean;
  clienteId: number;
  clienteNombre?: string;
}

export interface Movimiento {
  id?: number;
  fecha?: string;
  tipoMovimiento: string; // 'Retiro' or 'Deposito'
  valor: number;
  saldo?: number;
  cuentaNumero: string;
}

export interface MovimientoReporte {
  Fecha: string;
  Cliente: string;
  "Numero Cuenta": string;
  Tipo: string;
  "Saldo Inicial": number;
  Estado: boolean;
  Movimiento: number;
  "Saldo Disponible": number;
}

export interface ReporteResponse {
  pdf: string; // Base64 PDF
  reporte: MovimientoReporte[];
}
