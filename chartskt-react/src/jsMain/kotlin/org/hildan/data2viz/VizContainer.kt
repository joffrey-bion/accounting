package org.hildan.data2viz

import io.data2viz.charts.viz.*
import react.*
import react.dom.html.*
import web.dom.*

/**
 * Creates a [VizContainer] as a child of this component, which can then be configured as usual to set its size and
 * add a chart using the [init] lambda.
 */
fun ChildrenBuilder.vizContainer(init: VizContainer.() -> Unit) = VizContainerComponent {
    configurer = VizContainerConfigurer(init)
}

external interface VizContainerProps : Props {
    /**
     * An initialization function used to configure the [VizContainer] backing the [VizContainerComponent].
     *
     * Through this function, we can add a chart to the container using the properties and extensions provided in the
     * regular data2viz charts.kt library.
     */
    var configurer: VizContainerConfigurer
}

/**
 * Technical bridge to allow configuring a [VizContainer] as a React component. This trick allows to pass a function
 * with receiver (the [configure] function) as a React prop to the actual [VizContainerComponent].
 */
fun interface VizContainerConfigurer {
    fun VizContainer.configure()
}

/**
 * An internal global registry to keep track of [VizContainer]s corresponding to each div's ID.
 */
private val vizContainersByDivId = mutableMapOf<String, VizContainer>()

/**
 * A React component binding a VizContainer to the React tree.
 *
 * This is rather meant as a technical bridge, and it's public in case you need to pass this component around.
 * For more convenience when using it in regular React hierarchies, use the [vizContainer] extension function.
 */
val VizContainerComponent = FC<VizContainerProps>("VizContainer") { props ->
    val divId = "viz-container-${useId()}"
    useEffect(props.configurer) {
        println("RUNNING EFFECT in $divId")
        val vc = vizContainersByDivId.getOrPut(divId) {
            document.getElementById(divId).unsafeCast<org.w3c.dom.HTMLDivElement>().newVizContainer()
        }
        with(props.configurer) {
            vc.configure()
        }
    }
    ReactHTML.div {
        this.id = divId
    }
}
