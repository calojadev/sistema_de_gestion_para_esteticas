const API = "http://localhost:8080/api/clientes";
let ciAEliminar = null;
let modoEdicion = false;

// ── Cargar todos al iniciar ──
document.addEventListener("DOMContentLoaded", cargarTodos);

async function cargarTodos() {
  try {
    const res = await fetch(API);
    const clientes = await res.json();
    renderTabla(clientes);
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Buscar por CI ──
async function buscarPorCI() {
  const ci = document.getElementById("inputBuscarCI").value.trim();
  if (!ci) {
    cargarTodos();
    return;
  }
  try {
    const res = await fetch(`${API}/ci/${ci}`);
    if (res.status === 404) {
      mostrarAlerta("No se encontró ningún cliente con ese CI.", "warning");
      renderTabla([]);
      return;
    }
    const cliente = await res.json();
    renderTabla([cliente]);
  } catch (e) {
    mostrarAlerta("Error al buscar el cliente.", "danger");
  }
}

// ── Renderizar tabla ──
function renderTabla(clientes) {
  const tbody = document.getElementById("tablaClientes");
  if (clientes.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No se encontraron clientes.</td></tr>`;
    return;
  }
  tbody.innerHTML = clientes
    .map(
      (c) => `
    <tr>
      <td>${c.ci}</td>
      <td>${c.nombre}</td>
      <td>${c.apellido}</td>
      <td>${c.correo ?? "—"}</td>
      <td>${c.telefono ?? "—"}</td>
      <td>${c.direccion ?? "—"}</td>
      <td class="text-center">
        <button class="btn btn-warning btn-accion me-1" onclick="abrirModalEditar(${JSON.stringify(c).replace(/"/g, "&quot;")})">
          <i class="bi bi-pencil"></i>
        </button>
        <button class="btn btn-danger btn-accion" onclick="abrirModalEliminar(${c.ci})">
          <i class="bi bi-trash"></i>
        </button>
      </td>
    </tr>
  `,
    )
    .join("");
}

// ── Abrir modal nuevo ──
function abrirModalNuevo() {
  modoEdicion = false;
  document.getElementById("modalTitulo").textContent = "Nuevo Cliente";
  document.getElementById("inputCI").disabled = false;
  limpiarFormulario();
  new bootstrap.Modal(document.getElementById("modalCliente")).show();
}

// ── Abrir modal editar ──
function abrirModalEditar(cliente) {
  modoEdicion = true;
  document.getElementById("modalTitulo").textContent = "Editar Cliente";
  document.getElementById("inputCI").value = cliente.ci;
  document.getElementById("inputCI").disabled = true;
  document.getElementById("clienteCI").value = cliente.ci;
  document.getElementById("inputNombre").value = cliente.nombre;
  document.getElementById("inputApellido").value = cliente.apellido;
  document.getElementById("inputCorreo").value = cliente.correo ?? "";
  document.getElementById("inputTelefono").value = cliente.telefono ?? "";
  document.getElementById("inputDireccion").value = cliente.direccion ?? "";
  new bootstrap.Modal(document.getElementById("modalCliente")).show();
}

// ── Guardar (crear o actualizar) ──
async function guardarCliente() {
  const ci = document.getElementById("inputCI").value.trim();
  const nombre = document.getElementById("inputNombre").value.trim();
  const apellido = document.getElementById("inputApellido").value.trim();

  if (!ci || !nombre || !apellido) {
    mostrarAlerta("CI, nombre y apellido son obligatorios.", "warning");
    return;
  }

  const datos = {
    ci: parseInt(ci),
    nombre,
    apellido,
    correo: document.getElementById("inputCorreo").value.trim() || null,
    telefono: document.getElementById("inputTelefono").value.trim() || null,
    direccion: document.getElementById("inputDireccion").value.trim() || null,
  };

  try {
    let res;
    if (modoEdicion) {
      const ciOriginal = document.getElementById("clienteCI").value;
      res = await fetch(`${API}/ci/${ciOriginal}`, {
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
        document.getElementById("modalCliente"),
      ).hide();
      mostrarAlerta(
        modoEdicion
          ? "Cliente actualizado correctamente."
          : "Cliente creado correctamente.",
        "success",
      );
      cargarTodos();
    } else {
      mostrarAlerta(
        "Error al guardar. Verifica que el CI o correo no estén duplicados.",
        "danger",
      );
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Eliminar ──
function abrirModalEliminar(ci) {
  ciAEliminar = ci;
  new bootstrap.Modal(document.getElementById("modalEliminar")).show();
}

async function confirmarEliminar() {
  try {
    const res = await fetch(`${API}/ci/${ciAEliminar}`, { method: "DELETE" });
    bootstrap.Modal.getInstance(
      document.getElementById("modalEliminar"),
    ).hide();
    if (res.ok) {
      mostrarAlerta("Cliente eliminado correctamente.", "success");
      cargarTodos();
    } else {
      mostrarAlerta("Error al eliminar el cliente.", "danger");
    }
  } catch (e) {
    mostrarAlerta("Error al conectar con el servidor.", "danger");
  }
}

// ── Helpers ──
function limpiarFormulario() {
  [
    "inputCI",
    "inputNombre",
    "inputApellido",
    "inputCorreo",
    "inputTelefono",
    "inputDireccion",
  ].forEach((id) => (document.getElementById(id).value = ""));
}

function mostrarAlerta(mensaje, tipo) {
  const alerta = document.getElementById("alerta");
  alerta.className = `alert alert-${tipo}`;
  alerta.textContent = mensaje;
  alerta.classList.remove("d-none");
  setTimeout(() => alerta.classList.add("d-none"), 4000);
}
