# language: es
Característica: Gestión y Aceptación de Reservas de Carros

  Escenario: Un cliente calificado crea una reserva exitosamente
    Dado que se tiene un carro registrado con placa "ABC123", marca "Renault", modelo "Logan", año 2022, tipo "ECONOMICO" y tarifa diaria de 100000
    Y un cliente con documento "123", nombre "Ana Pérez", edad 30, licencia vigente, 5 años de licencia y sin deudas
    Cuando el cliente solicita crear una reserva para la placa "ABC123" desde "2026-03-02" hasta "2026-03-05"
    Entonces la reserva debe ser generada con éxito
    Y el carro con placa "ABC123" debe quedar en estado "RENTADO"

  Escenario: Se rechaza la reserva si el carro está en mantenimiento
    Dado que se tiene un carro registrado con placa "XYZ987", marca "Toyota", modelo "Corolla", año 2023, tipo "ECONOMICO" y tarifa diaria de 120000
    Y el carro "XYZ987" se encuentra en estado "MANTENIMIENTO"
    Y un cliente con documento "123", nombre "Ana Pérez", edad 30, licencia vigente, 5 años de licencia y sin deudas
    Cuando el cliente intenta reservar el carro con placa "XYZ987" desde "2026-03-02" hasta "2026-03-05"
    Entonces la reserva debe ser rechazada con el mensaje "El carro está en mantenimiento"