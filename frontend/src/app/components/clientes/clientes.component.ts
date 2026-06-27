import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clientes.component.html'
})
export class ClientesComponent implements OnInit {
  private clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  filteredClientes: Cliente[] = [];
  searchTerm: string = '';

  // Modal control
  isModalOpen: boolean = false;
  isEditMode: boolean = false;
  errorMessage: string = '';

  // Form Model
  currentCliente: Cliente = this.getEmptyCliente();

  ngOnInit() {
    this.loadClientes();
  }

  loadClientes() {
    this.clienteService.getAll().subscribe({
      next: (data) => {
        this.clientes = data;
        this.filterClientes();
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar clientes: ' + (err.error?.message || err.message);
      }
    });
  }

  filterClientes() {
    if (!this.searchTerm.trim()) {
      this.filteredClientes = this.clientes;
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredClientes = this.clientes.filter(c => 
      c.nombre.toLowerCase().includes(term) ||
      c.clienteid.toLowerCase().includes(term) ||
      c.identificacion.toLowerCase().includes(term)
    );
  }

  openNewModal() {
    this.isEditMode = false;
    this.currentCliente = this.getEmptyCliente();
    this.errorMessage = '';
    this.isModalOpen = true;
  }

  openEditModal(cliente: Cliente) {
    this.isEditMode = true;
    this.currentCliente = { ...cliente };
    this.errorMessage = '';
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  saveCliente() {
    this.errorMessage = '';
    if (this.isEditMode) {
      this.clienteService.update(this.currentCliente.id!, this.currentCliente).subscribe({
        next: () => {
          this.loadClientes();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al actualizar cliente';
        }
      });
    } else {
      this.clienteService.create(this.currentCliente).subscribe({
        next: () => {
          this.loadClientes();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al guardar cliente';
        }
      });
    }
  }

  deleteCliente(id: number) {
    if (confirm('¿Está seguro de eliminar este cliente? Se eliminarán todas sus cuentas y movimientos asociados.')) {
      this.clienteService.delete(id).subscribe({
        next: () => {
          this.loadClientes();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Error al eliminar cliente';
        }
      });
    }
  }

  private getEmptyCliente(): Cliente {
    return {
      nombre: '',
      genero: 'Masculino',
      edad: 30,
      identificacion: '',
      direccion: '',
      telefono: '',
      clienteid: '',
      contrasena: '',
      estado: true
    };
  }
}
