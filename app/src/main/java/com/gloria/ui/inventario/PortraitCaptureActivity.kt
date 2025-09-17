package com.gloria.ui.inventario

import com.journeyapps.barcodescanner.CaptureActivity

/**
 * Clase personalizada para forzar la orientación vertical en el escáner de código de barras
 */
class PortraitCaptureActivity : CaptureActivity() {
    // Esta clase sirve para forzar la orientación vertical
    // Hereda de CaptureActivity y mantiene la orientación portrait
}
