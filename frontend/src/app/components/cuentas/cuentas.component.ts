import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CuentaService } from '../../services/cuenta.service';
import { ClienteService } from '../../services/cliente.service';
import { Cuenta, Cliente } from '../../models';

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cuentas.component.html'
})
export class CuentasComponent implements OnInit {
  private cuentaService = inject(CuentaService);
  private clienteService = inject(ClienteService);

  cuentas: Cuenta[] = [];
  filteredCuentas: Cuenta[] = [];
  clientes: Cliente[] = [];
  searchTerm: string = '';

  isModalOpen: boolean = false;
  isEditMode: boolean = false;
  isDeleteModalOpen: boolean = false;
  cuentaToDeleteNumero: string | null = null;
  errorMessage: string = '';

  currentCuenta: Cuenta = this.getEmptyCuenta();

  ngOnInit() {
    this.loadCuentas();
    this.loadClientes();
  }

  loadCuentas() {
    this.cuentaService.getAll().subscribe({
      next: (data) => {
        this.cuentas = data;
        this.filteredCuentas = data;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar cuentas: ' + (err.error?.message || err.message);
      }
    });
  }

  loadClientes() {
    this.clienteService.getAll().subscribe({
      next: (data) => {
        this.clientes = data.filter(c => c.estado);
      }
    });
  }

  filterCuentas() {
    if (!this.searchTerm.trim()) {
      this.filteredCuentas = this.cuentas;
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredCuentas = this.cuentas.filter(c => 
      c.numeroCuenta.toLowerCase().includes(term) ||
      c.tipoCuenta.toLowerCase().includes(term) ||
      (c.clienteNombre && c.clienteNombre.toLowerCase().includes(term))
    );
  }

  openNewModal() {
    this.isEditMode = false;
    this.currentCuenta = this.getEmptyCuenta();
    if (this.clientes.length > 0) {
      this.currentCuenta.clienteId = this.clientes[0].id!;
    }
    this.errorMessage = '';
    this.isModalOpen = true;
  }

  openEditModal(cuenta: Cuenta) {
    this.isEditMode = true;
    this.currentCuenta = { ...cuenta };
    this.errorMessage = '';
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  saveCuenta() {
    this.errorMessage = '';
    if (this.isEditMode) {
      this.cuentaService.update(this.currentCuenta.numeroCuenta, this.currentCuenta).subscribe({
        next: () => {
          this.loadCuentas();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al actualizar cuenta';
        }
      });
    } else {
      this.cuentaService.create(this.currentCuenta).subscribe({
        next: () => {
          this.loadCuentas();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al guardar cuenta';
        }
      });
    }
  }

  openDeleteModal(numeroCuenta: string) {
    this.cuentaToDeleteNumero = numeroCuenta;
    this.isDeleteModalOpen = true;
  }

  closeDeleteModal() {
    this.isDeleteModalOpen = false;
    this.cuentaToDeleteNumero = null;
  }

  confirmDeleteCuenta() {
    if (this.cuentaToDeleteNumero !== null) {
      this.cuentaService.delete(this.cuentaToDeleteNumero).subscribe({
        next: () => {
          this.loadCuentas();
          this.closeDeleteModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al eliminar cuenta';
          this.closeDeleteModal();
        }
      });
    }
  }

  private getEmptyCuenta(): Cuenta {
    return {
      numeroCuenta: '',
      tipoCuenta: 'Ahorros',
      saldoInicial: 0.00,
      estado: true,
      clienteId: 0
    };
  }
}
