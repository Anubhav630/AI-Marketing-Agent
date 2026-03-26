import { useState, useEffect } from 'react';

export default function Toast({ id, message, variant, onDismiss }) {
    const [visible, setVisible] = useState(false);
    const [leaving, setLeaving] = useState(false);

    // Slide in on mount
    useEffect(() => {
        const frame = requestAnimationFrame(() => setVisible(true));
        return () => cancelAnimationFrame(frame);
    }, []);

    function handleDismiss() {
        setLeaving(true);
        setTimeout(() => onDismiss(id), 200);
    }

    return (
        <div
            className={`toast toast--${variant}${visible ? ' toast--visible' : ''}${leaving ? ' toast--leaving' : ''}`}
            onClick={handleDismiss}
            role="alert"
            aria-live="polite"
        >
            {message}
        </div>
    );
}
