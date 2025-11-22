import { Component, OnInit } from '@angular/core';
import { CitaService } from './service/cita.service';
import { MedicoService } from '../medico/service/medico.service';
import { PacienteService } from '../paciente/service/paciente.service';
import { Cita } from './models/cita';
import { Paciente } from '../paciente/models/paciente';
import { Medico } from '../medico/models/medico';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import Modal from 'bootstrap/js/dist/modal';
import { FilterCitasPipe } from './pipes/filter-cita.pipe';
import { NgxSpinnerModule, NgxSpinnerService } from "ngx-spinner";

@Component({
  selector: 'app-citas',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, FilterCitasPipe, NgxSpinnerModule],
  templateUrl: './cita.component.html',
  styleUrls: ['./cita.component.scss']
})
export class CitaComponent implements OnInit {
  modalInstance: Modal | null = null;
  modoFormulario = '';
  titleModal = '';
  titleBoton = '';
  citasList: Cita[] = [];
  pacientesList: Paciente[] = [];
  medicosList: Medico[] = [];
  citaSelected: Cita | null = null;
  titleSpinner: string = 'Cargando...';

  filtroColumna: string = '';

  form: FormGroup;

  today = new Date().toISOString().split('T')[0];

  ngOnInit() {
    this.listarCitas();
    this.listarPacientes();
    this.listarMedicos();
  }

  constructor(
    private readonly citaService: CitaService,
    private readonly pacienteService: PacienteService,
    private readonly medicoService: MedicoService,
    private readonly formBuilder: FormBuilder,
    private readonly spinner: NgxSpinnerService,
  ) {
    this.form = this.formBuilder.group({
      pacienteId: ['', Validators.required],
      medicoId: ['', Validators.required],
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      estado: ['', Validators.required],
      motivo: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(200)]],
    });

    this.listarCitas();
    this.spinner.show();

    setTimeout(() => {
      /** spinner ends after 5 seconds */
      this.spinner.hide();
    }, 5000);

  }

  listarCitas() {
    this.citaService.listarCitas().subscribe({
      next: (data) => (this.citasList = data),
      error: (err) => console.error('Error al listar citas', err),
    });
  }

  listarPacientes() {
    this.pacienteService.listarPacientes().subscribe({

      next: (data) => (this.pacientesList = data),

      error: (err) => console.error('Error al listar pacientes', err),
    })
  }

  listarMedicos() {
    this.medicoService.listarMedicos().subscribe({
      next: (data) => (this.medicosList = data),
      error: (err) => console.error('Error al listar medicos', err),
    });
  }

  closeModal() {
    this.modalInstance?.hide();
  }

  openModal(modo: string) {
    this.titleModal = modo === 'C' ? 'Crear Cita' : 'Editar Cita';
    this.titleBoton = modo === 'C' ? 'Guardar Cita' : 'Actualizar Cita';
    this.modoFormulario = modo;
    const modalElement = document.getElementById('modalCrearCita');
    if (modalElement) {
      this.modalInstance ??= new Modal(modalElement);
      this.modalInstance.show();
    }
  }

  abrirNuevaCita() {
    this.citaSelected = null;
    this.form.reset();
    this.openModal('C');
  }

  abrirEditarCita(cita: Cita) {
    this.citaSelected = cita;
    const [fecha, hora] = cita.fechaHora.split('T');
    this.form.patchValue({
      pacienteId: cita.paciente?.id,
      medicoId: cita.medico?.id,
      fecha,
      hora,
      estado: cita.estado,
      motivo: cita.motivo,
    });
    this.openModal('E');
  }

  guardarCita() {
    if (this.form.invalid) {
      Swal.fire('Error', 'Por favor completa todos los campos requeridos', 'error');
      return;
    }

    const { fecha, hora, pacienteId, medicoId, motivo, estado } = this.form.value;
    const cita: Cita = {
      pacienteId,
      medicoId,
      fechaHora: `${fecha}T${hora}:00`,
      motivo,
      estado
    };


    this.citaService.guardarCita(cita).subscribe({
      next: (data) => {
        Swal.fire('Éxito', data.mensaje, 'success');
        this.closeModal();
        this.listarCitas();
      },
      error: (error) => {
        console.error('Error al guardar cita:', error);
        Swal.fire('Error', error.error?.message || 'Error al guardar la cita', 'error');
      },
    });
  }
}
