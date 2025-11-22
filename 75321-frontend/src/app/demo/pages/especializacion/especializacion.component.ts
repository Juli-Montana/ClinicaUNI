import { Component } from '@angular/core';
import { Especializacion } from './models/especializacion';
import { EspecializacionService } from './service/especializacion.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';

import Modal from 'bootstrap/js/dist/modal';
import Swal from 'sweetalert2';
import { FilterEspecializacionesPipe } from './pipes/filter-especializaciones.pipe';
import { NgxSpinnerModule, NgxSpinnerService } from "ngx-spinner";

@Component({
  selector: 'app-especializacion',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, FilterEspecializacionesPipe, NgxSpinnerModule],
  templateUrl: './especializacion.component.html',
  styleUrls: ['./especializacion.component.scss']
})
export class EspecializacionComponent {
  modalInstance: Modal | null = null;
  modoFormulario = '';
  titleModal = '';
  titleBoton = '';
  especializacionList: Especializacion[] = [];
  especializacionSelected: Especializacion | null = null;
  titleSpinner: string = 'Cargando...';

  filtroColumna: string = '';

  form: FormGroup;

  constructor(
    private readonly especializacionService: EspecializacionService,
    private readonly formBuilder: FormBuilder,
    private readonly spinner: NgxSpinnerService,
  ) {
    this.form = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
      codigoEspecializacion: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20)]],
    });

    this.listarEspecializaciones();
    this.spinner.show();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 5000);

  }

  listarEspecializaciones() {
    this.especializacionService.listarEspecializaciones().subscribe({
      next: (data) => (this.especializacionList = data),
      error: (err) => console.error('Error al listar especializaciones', err),
    });
  }

  closeModal() {
    this.modalInstance?.hide();
  }

  openModal(modo: string) {
    this.titleModal = modo === 'C' ? 'Crear Especialización' : 'Editar Especialización';
    this.titleBoton = modo === 'C' ? 'Guardar Especialización' : 'Actualizar Especialización';
    this.modoFormulario = modo;
    const modalElement = document.getElementById('modalCrearEspecializacion');
    if (modalElement) {
      this.modalInstance ??= new Modal(modalElement);
      this.modalInstance.show();
    }
  }

  abrirNuevaEspecializacion() {
    this.especializacionSelected = null;
    this.form.reset();
    this.openModal('C');
  }

  abrirEditarEspecializacion(especializacion: Especializacion) {
    this.especializacionSelected = especializacion;
    this.form.patchValue({
      nombre: especializacion.nombre,
      descripcion: especializacion.descripcion,
      codigoEspecializacion: especializacion.codigoEspecializacion,
    });
    this.openModal('E');
  }

  guardarEspecializacion() {
    if (this.form.invalid) {
      Swal.fire('Error', 'Por favor completa todos los campos requeridos', 'error');
      return;
    }

    const especializacion: Especializacion = this.form.value;

    this.especializacionService.guardarEspecializacion(especializacion).subscribe({
      next: (data) => {
        Swal.fire('Éxito', data.mensaje, 'success');
        this.closeModal();
        this.listarEspecializaciones();
      },
      error: (err) => {
        console.error('Error al guardar especialización', err);
        Swal.fire('Error', 'No se pudo guardar o actualizar la especialización', 'error');
      },
    });
  }
}
