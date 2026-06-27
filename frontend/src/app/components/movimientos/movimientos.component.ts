import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MovimientoService } from '../../services/movimiento.service';
import { CuentaService } from '../../services/cuenta.service';
import { Movimiento, Cuenta } from '../../models';

@Component({
  selector: 'app-movimientos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './movimientos.component.html'
})
export class MovimientosComponent implements OnInit {
  private movimientoService = inject(MovimientoService);
  private cuentaService = inject(CuentaService);

  movimientos: Movimiento[] = [];
  filteredMovimientos: Movimiento[] = [];
  cuentas: Cuenta[] = [];
  searchTerm: string = '';

  isModalOpen: boolean = false;
  isEditMode: boolean = false;
  errorMessage: string = '';

  // Form Fields
  selectedCuentaNumero: string = '';
  tipoMovimiento: string = 'Deposito';
  valorAbs: number = 0;
  currentMovimientoId: number | null = null;

  ngOnInit() {
    this.loadMovimientos();
    this.loadCuentas();
  }

  loadMovimientos() {
    this.movimientoService.getAll().subscribe({
      next: (data) => {
        // Sort by date desc
        this.movimientos = data.sort((a, b) => 
          new Date(b.fecha!).getTime() - new Date(a.fecha!).getTime()
        );
        this.filteredMovimientos = this.movimientos;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar movimientos: ' + (err.error?.message || err.message);
      }
    });
  }

  loadCuentas() {
    this.cuentaService.getAll().subscribe({
      next: (data) => {
        this.cuentas = data.filter(c => c.estado);
      }
    });
  }

  filterMovimientos() {
    if (!this.searchTerm.trim()) {
      this.filteredMovimientos = this.movimientos;
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredMovimientos = this.movimientos.filter(m => 
      m.cuentaNumero.toLowerCase().includes(term) ||
      m.tipoMovimiento.toLowerCase().includes(term) ||
      m.valor.toString().includes(term)
    );
  }

  openNewModal() {
    this.isEditMode = false;
    this.errorMessage = '';
    this.selectedCuentaNumero = this.cuentas.length > 0 ? this.cuentas[0].numeroCuenta : '';
    this.tipoMovimiento = 'Deposito';
    this.valorAbs = 0;
    this.currentMovimientoId = null;
    this.isModalOpen = true;
  }

  openEditModal(mov: Movimiento) {
    this.isEditMode = true;
    this.errorMessage = '';
    this.currentMovimientoId = mov.id!;
    this.selectedCuentaNumero = mov.cuentaNumero;
    this.tipoMovimiento = mov.valor < 0 ? 'Retiro' : 'Deposito';
    this.valorAbs = Math.abs(mov.valor);
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  saveMovimiento() {
    this.errorMessage = '';
    
    // Convert absolute value to negative if it is a Withdrawal (Retiro)
    const finalValor = this.tipoMovimiento === 'Retiro' ? -Math.abs(this.valorAbs) : Math.abs(this.valorAbs);

    const movDto: Movimiento = {
      cuentaNumero: this.selectedCuentaNumero,
      tipoMovimiento: this.tipoMovimiento,
      valor: finalValor
    };

    if (this.isEditMode) {
      movDto.id = this.currentMovimientoId!;
      this.movimientoService.update(this.currentMovimientoId!, movDto).subscribe({
        next: () => {
          this.loadMovimientos();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al actualizar movimiento';
        }
      });
    } else {
      this.movimientoService.create(movDto).subscribe({
        next: () => {
          this.loadMovimientos();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al realizar transacción';
        }
      });
    }
  }

  deleteMovimiento(id: number) {
    if (confirm('¿Está seguro de eliminar este registro de movimiento?')) {
      this.movimientoService.delete(id).subscribe({
        next: () => {
          this.loadMovimientos();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al eliminar movimiento';
        }
      });
    }
  }
}
