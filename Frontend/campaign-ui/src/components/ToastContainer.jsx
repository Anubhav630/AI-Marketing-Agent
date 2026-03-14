import Toast from './Toast';

export default function ToastContainer({ toasts, onDismiss }) {
    return (
        <div
            className="toast-container"
            style={{
                position: 'fixed',
                bottom: 'var(--space-6)',
                right: 'var(--space-6)',
                display: 'flex',
                flexDirection: 'column',
                gap: 'var(--space-2)',
                zIndex: 1000,
            }}
        >
            {toasts.map((toast) => (
                <Toast
                    key={toast.id}
                    id={toast.id}
                    message={toast.message}
                    variant={toast.variant}
                    onDismiss={onDismiss}
                />
            ))}
        </div>
    );
}
