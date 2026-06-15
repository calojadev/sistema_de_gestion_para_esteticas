const API = "http://localhost:8080/api/reservas";
let idAEliminar = null;
let modoEdicion = false;

document.addEventListener("DOMContentLoaded", () => {
  cargarTodos();
  cargarClientes();
  cargarProfesionales();
  cargarServicios();
});

// ── Cargar datos para los selectores ──
async function cargarClientes() {
  const res = await fetch("http://localhost:8080/api/clientes");
  const clientes = await res.json();
  const select = document.getElementById("inputCliente");
  clientes.forEach((c) => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.textContent = `${c.nombre} ${c.apellido} (CI: ${c.ci})`;
    select.appendChild(opt);
  });
}

async function cargarProfesionales() {
  const res = await fetch("http://localhost:8080/api/profesionales");
  const profesionales = await res.json();
  const select = document.getElementById("inputProfesional");
  profesionales.forEach((p) => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.nombre} ${p.apellido} — ${p.especialidad}`;
    select.appendChild(opt);
  });
}

async function cargarServicios() {
  const res = await fetch("http://localhost:8080/api/servicios");
  const servicios = await res.json();
  const select = document.getElementById("inputServicios");
  servicios.forEach((s) => {
    const opt = document.createElement("option");
    opt.value = s.id;
    opt.textContent = `${s.nombre} — Gs. ${Number(s.precio).toLocaleString()}`;
    select.appendChild(opt);
  });
}

// ── Cargar todas las reservas ──
async function cargarTodos() {
  try {
    const res = await fetch(API);
    const reservas = await res.json();
    renderTabla(reservas);
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Filtrar por estado ──
async function filtrarPorEstado() {
  const estado = document.getElementById("selectFiltroEstado").value;
  if (!estado) {
    cargarTodos();
    return;
  }
  try {
    const res = await fetch(`${API}/estado/${estado}`);
    const reservas = await res.json();
    renderTabla(reservas);
  } catch (e) {
    mostrarAlerta("Error al filtrar.", "danger");
  }
}

// ── Renderizar tabla ──
function renderTabla(reservas) {
  const tbody = document.getElementById("tablaReservas");
  if (reservas.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No se encontraron reservas.</td></tr>`;
    return;
  }
  tbody.innerHTML = reservas
    .map(
      (r) => `
    <tr>
      <td>${r.fecha}</td>
      <td>${r.hora}</td>
      <td>${r.cliente ? `${r.cliente.nombre} ${r.cliente.apellido}` : "—"}</td>
      <td>${r.profesional ? `${r.profesional.nombre} ${r.profesional.apellido}` : "—"}</td>
      <td>${badgeEstado(r.estado)}</td>
      <td>${r.observaciones ?? "—"}</td>
      <td class="text-center">
        <button class="btn btn-info btn-accion me-1 text-white" onclick="cambiarEstado(${r.id}, '${r.estado}')">
          <i class="bi bi-arrow-repeat"></i>
        </button>
        <button class="btn btn-warning btn-accion me-1" onclick="abrirModalEditar(${JSON.stringify(r).replace(/"/g, "&quot;")})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-danger btn-accion" onclick="abrirModalEliminar(${r.id})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `,
    )
    .join("");
}

function badgeEstado(estado) {
  const clases = {
    PENDIENTE: "badge-pendiente",
    CONFIRMADA: "badge-confirmada",
    CANCELADA: "badge-cancelada",
    COMPLETADA: "badge-completada",
  };
  return `<span class="badge ${clases[estado] ?? "bg-secondary"}">${estado}</span>`;
}

// ── Modal nuevo ──
function abrirModalNuevo() {
  modoEdicion = false;
  document.getElementById("modalTitulo").textContent = "Nueva Reserva";
  document.getElementById("inputFecha").min = new Date()
    .toISOString()
    .split("T")[0];
  limpiarFormulario();
  new bootstrap.Modal(document.getElementById("modalReserva")).show();
}

// ── Modal editar ──
function abrirModalEditar(r) {
  modoEdicion = true;
  document.getElementById("modalTitulo").textContent = "Editar Reserva";
  document.getElementById("reservaId").value = r.id;
  document.getElementById("inputFecha").value = r.fecha;
  document.getElementById("inputHora").value = r.hora;
  document.getElementById("inputCliente").value = r.cliente?.id ?? "";
  document.getElementById("inputProfesional").value = r.profesional?.id ?? "";
  document.getElementById("inputObservaciones").value = r.observaciones ?? "";
  new bootstrap.Modal(document.getElementById("modalReserva")).show();
}

// ── Guardar reserva ──
async function guardarReserva() {
  const clienteId = document.getElementById("inputCliente").value;
  const profesionalId = document.getElementById("inputProfesional").value;
  const fecha = document.getElementById("inputFecha").value;
  const hora = document.getElementById("inputHora").value;

  if (!clienteId || !profesionalId || !fecha || !hora) {
    mostrarAlerta(
      "Cliente, profesional, fecha y hora son obligatorios.",
      "warning",
    );
    return;
  }

  // Obtener servicios seleccionados
  const serviciosSeleccionados = Array.from(
    document.getElementById("inputServicios").selectedOptions,
  ).map((opt) => ({ servicio: { id: parseInt(opt.value) }, cantidad: 1 }));

  if (serviciosSeleccionados.length === 0) {
    mostrarAlerta("Debes seleccionar al menos un servicio.", "warning");
    return;
  }

  const datos = {
    fecha,
    hora,
    observaciones:
      document.getElementById("inputObservaciones").value.trim() || null,
    cliente: { id: parseInt(clienteId) },
    profesional: { id: parseInt(profesionalId) },
    reservaServicios: serviciosSeleccionados,
  };

  try {
    let res;
    if (modoEdicion) {
      const id = document.getElementById("reservaId").value;
      res = await fetch(`${API}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
      });
    } else {
      res = await fetch(API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
      });
    }

    if (res.ok) {
      bootstrap.Modal.getInstance(
        document.getElementById("modalReserva"),
      ).hide();
      mostrarAlerta(
        modoEdicion
          ? "Reserva actualizada correctamente."
          : "Reserva creada correctamente.",
        "success",
      );
      cargarTodos();
    } else {
      const err = await res.text();
      mostrarAlerta(
        "Error: " + (err || "Verifica disponibilidad del profesional."),
        "danger",
      );
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Cambiar estado ──
async function cambiarEstado(id, estadoActual) {
  const estados = ["PENDIENTE", "CONFIRMADA", "COMPLETADA", "CANCELADA"];
  const opciones = estados
    .filter((e) => e !== estadoActual)
    .map((e) => `<option value="${e}">${e}</option>`)
    .join("");

  const select = document.createElement("select");
  select.className = "form-select form-select-sm";
  select.innerHTML = `<option value="">Seleccionar...</option>${opciones}`;

  const { value: nuevoEstado } = await Swal.fire({
    title: "Cambiar estado",
    html: `<p class="mb-2">Estado actual: <strong>${estadoActual}</strong></p>${select.outerHTML}`,
    confirmButtonText: "Confirmar",
    showCancelButton: true,
    cancelButtonText: "Cancelar",
    preConfirm: () =>
      document.querySelector(".swal2-html-container select").value,
  }).catch(() => ({ value: null }));

  if (!nuevoEstado) return;

  try {
    const res = await fetch(`${API}/${id}/estado/${nuevoEstado}`, {
      method: "PATCH",
    });
    if (res.ok) {
      mostrarAlerta("Estado actualizado correctamente.", "success");
      cargarTodos();
    } else {
      mostrarAlerta("No se pudo cambiar el estado.", "danger");
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Eliminar ──
function abrirModalEliminar(id) {
  idAEliminar = id;
  new bootstrap.Modal(document.getElementById("modalEliminar")).show();
}

async function confirmarEliminar() {
  try {
    const res = await fetch(`${API}/${idAEliminar}`, { method: "DELETE" });
    bootstrap.Modal.getInstance(
      document.getElementById("modalEliminar"),
    ).hide();
    if (res.ok) {
      mostrarAlerta("Reserva eliminada correctamente.", "success");
      cargarTodos();
    } else {
      mostrarAlerta("Error al eliminar la reserva.", "danger");
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Helpers ──
function limpiarFormulario() {
  ["reservaId", "inputFecha", "inputHora", "inputObservaciones"].forEach(
    (id) => (document.getElementById(id).value = ""),
  );
  document.getElementById("inputCliente").value = "";
  document.getElementById("inputProfesional").value = "";
  Array.from(document.getElementById("inputServicios").options).forEach(
    (opt) => (opt.selected = false),
  );
}

function mostrarAlerta(mensaje, tipo) {
  const alerta = document.getElementById("alerta");
  alerta.className = `alert alert-${tipo}`;
  alerta.textContent = mensaje;
  alerta.classList.remove("d-none");
  setTimeout(() => alerta.classList.add("d-none"), 4000);
}
