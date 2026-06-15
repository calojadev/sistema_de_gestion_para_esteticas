const API = "http://localhost:8080/api/profesionales";
let idAEliminar = null;
let modoEdicion = false;

document.addEventListener("DOMContentLoaded", cargarTodos);

async function cargarTodos() {
  try {
    const res = await fetch(API);
    const profesionales = await res.json();
    renderTabla(profesionales);
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

async function buscarPorEspecialidad() {
  const especialidad = document
    .getElementById("inputBuscarEspecialidad")
    .value.trim();
  if (!especialidad) {
    cargarTodos();
    return;
  }
  try {
    const res = await fetch(`${API}/especialidad/${especialidad}`);
    const profesionales = await res.json();
    if (profesionales.length === 0) {
      mostrarAlerta(
        "No se encontraron profesionales con esa especialidad.",
        "warning",
      );
    }
    renderTabla(profesionales);
  } catch (e) {
    mostrarAlerta("Error al buscar.", "danger");
  }
}

function renderTabla(profesionales) {
  const tbody = document.getElementById("tablaProfesionales");
  if (profesionales.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron profesionales.</td></tr>`;
    return;
  }
  tbody.innerHTML = profesionales
    .map(
      (p) => `
    <tr>
      <td>${p.nombre}</td>
      <td>${p.apellido}</td>
      <td><span class="badge bg-secondary">${p.especialidad}</span></td>
      <td>${p.correo ?? "—"}</td>
      <td>${p.telefono ?? "—"}</td>
      <td class="text-center">
        <button class="btn btn-warning btn-accion me-1" onclick="abrirModalEditar(${JSON.stringify(p).replace(/"/g, "&quot;")})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-danger btn-accion" onclick="abrirModalEliminar(${p.id})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `,
    )
    .join("");
}

function abrirModalNuevo() {
  modoEdicion = false;
  document.getElementById("modalTitulo").textContent = "Nuevo Profesional";
  limpiarFormulario();
  new bootstrap.Modal(document.getElementById("modalProfesional")).show();
}

function abrirModalEditar(p) {
  modoEdicion = true;
  document.getElementById("modalTitulo").textContent = "Editar Profesional";
  document.getElementById("profesionalId").value = p.id;
  document.getElementById("inputNombre").value = p.nombre;
  document.getElementById("inputApellido").value = p.apellido;
  document.getElementById("inputEspecialidad").value = p.especialidad;
  document.getElementById("inputCorreo").value = p.correo ?? "";
  document.getElementById("inputTelefono").value = p.telefono ?? "";
  new bootstrap.Modal(document.getElementById("modalProfesional")).show();
}

async function guardarProfesional() {
  const nombre = document.getElementById("inputNombre").value.trim();
  const apellido = document.getElementById("inputApellido").value.trim();
  const especialidad = document
    .getElementById("inputEspecialidad")
    .value.trim();

  if (!nombre || !apellido || !especialidad) {
    mostrarAlerta(
      "Nombre, apellido y especialidad son obligatorios.",
      "warning",
    );
    return;
  }

  const datos = {
    nombre,
    apellido,
    especialidad,
    correo: document.getElementById("inputCorreo").value.trim() || null,
    telefono: document.getElementById("inputTelefono").value.trim() || null,
  };

  try {
    let res;
    if (modoEdicion) {
      const id = document.getElementById("profesionalId").value;
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
        document.getElementById("modalProfesional"),
      ).hide();
      mostrarAlerta(
        modoEdicion
          ? "Profesional actualizado correctamente."
          : "Profesional creado correctamente.",
        "success",
      );
      cargarTodos();
    } else {
      mostrarAlerta(
        "Error al guardar. Verifica los datos ingresados.",
        "danger",
      );
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

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
      mostrarAlerta("Profesional eliminado correctamente.", "success");
      cargarTodos();
    } else {
      mostrarAlerta("Error al eliminar el profesional.", "danger");
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

function limpiarFormulario() {
  [
    "profesionalId",
    "inputNombre",
    "inputApellido",
    "inputEspecialidad",
    "inputCorreo",
    "inputTelefono",
  ].forEach((id) => (document.getElementById(id).value = ""));
}

function mostrarAlerta(mensaje, tipo) {
  const alerta = document.getElementById("alerta");
  alerta.className = `alert alert-${tipo}`;
  alerta.textContent = mensaje;
  alerta.classList.remove("d-none");
  setTimeout(() => alerta.classList.add("d-none"), 4000);
}
